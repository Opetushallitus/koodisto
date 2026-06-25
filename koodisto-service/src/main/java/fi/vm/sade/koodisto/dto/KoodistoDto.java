package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Tila;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "error.codesGroupUri.blank")
    private String codesGroupUri;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Min(value = 0, message = "error.version.less.than.zero")
    private Long version;
    @Min(value = 1, message = "error.versio.less.than.one")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    private int versio;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date paivitysPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String paivittajaOid;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @NotNull(message = "error.voimassaAlkuPvm.empty")
    private Date voimassaAlkuPvm;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date voimassaLoppuPvm;
    @NotNull(message = "error.tila.empty")
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Tila tila;
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    @NotEmpty(message = "error.metadata.empty")
    @Valid
    private List<KoodistoMetadataDto> metadata = new ArrayList<>();
    @JsonView(JsonViews.Extended.class)
    private List<Integer> codesVersions;

    @JsonIgnore
    @AssertTrue(message = "error.validation.enddate")
    public boolean isStartBeforeEnd() {
        return Optional.ofNullable(voimassaLoppuPvm).map(date -> date.after(voimassaAlkuPvm)).orElse(true);
    }

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

        @JsonCreator
        public RelationCodes(
                @JsonProperty("codesUri") String codesUri,
                @JsonProperty("codesVersion") Integer codesVersion,
                @JsonProperty("passive") Boolean passive,
                @JsonProperty("nimi") Map<String, String> nimi,
                @JsonProperty("kuvaus") Map<String, String> kuvaus
        ) {
            this.codesUri = codesUri;
            this.codesVersion = codesVersion;
            this.passive = Boolean.TRUE.equals(passive);
            this.nimi = nimi;
            this.kuvaus = kuvaus;
        }
    }
}
