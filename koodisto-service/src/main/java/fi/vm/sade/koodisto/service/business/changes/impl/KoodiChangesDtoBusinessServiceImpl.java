package fi.vm.sade.koodisto.service.business.changes.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;

@Service
public class KoodiChangesDtoBusinessServiceImpl implements KoodiChangesDtoBusinessService {
    
    @Autowired
    private KoodiBusinessService service;

    @Override
    public KoodiChangesDto getChangesDto(String uri, Integer versio) {
        KoodiVersio koodiVersio = service.getKoodiVersio(uri, versio);
        KoodiVersio latestKoodiVersio = service.getLatestKoodiVersio(uri);
        return new KoodiChangesDto(KoodiChangesDto.MuutosTila.EI_MUUTOKSIA, latestKoodiVersio.getVersio(), null, null, null, null, null, null, null);
    }

}
