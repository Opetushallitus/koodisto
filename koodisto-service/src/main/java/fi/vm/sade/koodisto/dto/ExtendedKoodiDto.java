package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedKoodiDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date paivitysPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected String paivittajaOid;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    @NotNull
    protected Date voimassaAlkuPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date voimassaLoppuPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    @NotNull
    protected Tila tila;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotEmpty(message = "error.metadata.empty")
    protected List<KoodiMetadataDto> metadata = new ArrayList<>();
    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotNull
    protected List<RelationCodeElement> withinCodeElements = new ArrayList<>();
    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotNull
    protected List<RelationCodeElement> includesCodeElements = new ArrayList<>();
    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotNull
    protected List<RelationCodeElement> levelsWithCodeElements = new ArrayList<>();
    @JsonView({JsonViews.Internal.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer versions;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotBlank
    private String koodiUri;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private String resourceUri;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @Min(0)
    private int versio;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private KoodistoItemDto koodisto;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
    @NotBlank
    private String koodiArvo;

    @JsonIgnore
    @AssertTrue(message = "error.validation.enddate")
    public boolean startBeforeEnd() {
        return Optional.ofNullable(voimassaLoppuPvm).map(date -> date.after(voimassaAlkuPvm)).orElse(true);
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class RelationCodeElement {
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
        private final String codeElementUri;
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
        private final Integer codeElementVersion;
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
        private final String codeElementValue;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
        private final List<SimpleMetadataDto> relationMetadata;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
        private final List<SimpleMetadataDto> parentMetadata;

        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class, JsonViews.SimpleWithRelations.class})
        private final boolean passive;
    }
}
