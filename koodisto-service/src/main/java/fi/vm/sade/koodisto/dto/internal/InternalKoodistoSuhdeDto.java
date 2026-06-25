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
public class InternalKoodistoSuhdeDto {
    @JsonView({JsonViews.Internal.class})
    private final String koodistoUri;
    @JsonView({JsonViews.Internal.class})
    private final Integer koodistoVersio;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> nimi;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> kuvaus;

    @JsonCreator
    public InternalKoodistoSuhdeDto(
            @JsonProperty("koodistoUri") String koodistoUri,
            @JsonProperty("koodistoVersio") Integer koodistoVersio,
            @JsonProperty("nimi") Map<String, String> nimi,
            @JsonProperty("kuvaus") Map<String, String> kuvaus
    ) {
        this.koodistoUri = koodistoUri;
        this.koodistoVersio = koodistoVersio;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
    }
}
