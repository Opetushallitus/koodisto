package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Builder;

import java.util.Map;

@Builder
public class InternalKoodisuhdeDto {
    @JsonView({JsonViews.Intetrnal.class})
    private final String koodiUri;
    @JsonView({JsonViews.Intetrnal.class})
    private final Integer koodiVersio;
    @JsonView({JsonViews.Intetrnal.class})
    private final Map<String, String> nimi;
    @JsonView({JsonViews.Intetrnal.class})
    private final Map<String, String> kuvaus;
}