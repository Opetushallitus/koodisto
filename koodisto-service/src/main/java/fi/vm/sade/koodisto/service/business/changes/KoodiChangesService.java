package fi.vm.sade.koodisto.service.business.changes;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.model.KoodiVersio;

public interface KoodiChangesService extends ChangesService<KoodiChangesDto> {

    KoodiChangesDto constructChangesDto(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio);

}
