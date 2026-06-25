package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class InternalKoodiSuhdeDto {
    @JsonView({JsonViews.Internal.class})
    private final String koodiUri;
    @JsonView({JsonViews.Internal.class})
    private final Integer koodiVersio;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> nimi;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> kuvaus;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> koodistoNimi;

    @JsonCreator
    public InternalKoodiSuhdeDto(
            @JsonProperty("koodiUri") String koodiUri,
            @JsonProperty("koodiVersio") Integer koodiVersio,
            @JsonProperty("nimi") Map<String, String> nimi,
            @JsonProperty("kuvaus") Map<String, String> kuvaus,
            @JsonProperty("koodistoNimi") Map<String, String> koodistoNimi
    ) {
        this.koodiUri = koodiUri;
        this.koodiVersio = koodiVersio;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.koodistoNimi = koodistoNimi;
    }
}
