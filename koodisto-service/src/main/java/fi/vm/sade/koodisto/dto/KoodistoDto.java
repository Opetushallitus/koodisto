package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
public class KoodistoDto extends AbstractKoodistoDto {
    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodes> withinCodes = new ArrayList<>();
    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodes> includesCodes = new ArrayList<>();
    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodes> levelsWithCodes = new ArrayList<>();
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String codesGroupUri;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private int versio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date paivitysPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String paivittajaOid;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @NotNull
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date voimassaLoppuPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Tila tila;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    @NotEmpty(message = "error.metadata.empty")
    private List<KoodistoMetadata> metadata = new ArrayList<>();
    @JsonView(JsonViews.Extended.class)
    private List<Integer> codesVersions;

    @JsonIgnore
    @AssertTrue(message = "error.validation.enddate")
    public boolean startBeforeEnd() {
        return Optional.ofNullable(voimassaLoppuPvm).map(date -> date.after(voimassaAlkuPvm)).orElse(true);
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    public static class RelationCodes {
        @JsonView({JsonViews.Extended.class})
        private final String codesUri;
        @JsonView({JsonViews.Extended.class})
        private final Integer codesVersion;
        @JsonView({JsonViews.Extended.class})
        private final boolean passive;
        @JsonView({JsonViews.Extended.class})
        private final Map<String, String> nimi;
        @JsonView({JsonViews.Extended.class})
        private final Map<String, String> kuvaus;
    }
}
