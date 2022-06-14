package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExtendedKoodiDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String koodiUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private int versio;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private KoodistoItemDto koodisto;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private String koodiArvo;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date paivitysPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected String paivittajaOid;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date voimassaAlkuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Date voimassaLoppuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Internal.class})
    protected Tila tila;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    protected List<KoodiMetadataDto> metadata = new ArrayList<>();

    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
    protected List<RelationCodeElement> withinCodeElements = new ArrayList<>();

    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
    protected List<RelationCodeElement> includesCodeElements = new ArrayList<>();

    @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
    protected List<RelationCodeElement> levelsWithCodeElements = new ArrayList<>();

    @JsonView({JsonViews.Internal.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer versions;

    @Getter
    @RequiredArgsConstructor
    public static class RelationCodeElement {
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
        private final String codeElementUri;
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
        private final Integer codeElementVersion;
        @JsonView({JsonViews.Extended.class, JsonViews.Internal.class})
        private final String codeElementValue;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
        private final List<SimpleMetadataDto> relationMetadata;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
        private final List<SimpleMetadataDto> parentMetadata;

        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
        private final boolean passive;
    }
}
