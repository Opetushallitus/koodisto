package fi.vm.sade.koodisto.dto.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import lombok.Builder;

import java.util.Map;

@Builder
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
}