package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.views.JsonViews;
import lombok.Builder;

import java.util.Map;

@Builder
public class InternalKoodistoSuhdeDto {
    @JsonView({JsonViews.Internal.class})
    private final String koodistoUri;
    @JsonView({JsonViews.Internal.class})
    private final Integer koodistoVersio;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> nimi;
    @JsonView({JsonViews.Internal.class})
    private final Map<String, String> kuvaus;
}
