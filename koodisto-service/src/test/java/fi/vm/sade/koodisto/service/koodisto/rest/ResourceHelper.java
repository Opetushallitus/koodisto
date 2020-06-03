package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceHelper {

    @Autowired
    private CodesResource resource;

    @Autowired
    private CodeElementResource codeElementResource;

    public KoodistoDto createKoodisto(KoodistoDto koodisto) {
        return resource.insert(koodisto);
    }

    public KoodistoDto getKoodisto(String koodistoUri, int versio) {
        return resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
    }

    public KoodistoDto updateKoodisto(KoodistoDto koodisto) {
        int versio = resource.update(koodisto);
        return getKoodisto(koodisto.getKoodistoUri(), versio);
    }

    public KoodiDto createKoodi(String koodistoUri, KoodiDto koodi) {
        return codeElementResource.insert(koodistoUri, koodi);
    }

    public ExtendedKoodiDto getKoodi(String koodiUri, int versio) {
        return codeElementResource.getCodeElementByUriAndVersion(koodiUri, versio);
    }

    public KoodiDto updateKoodi(KoodiDto koodi) {
        return codeElementResource.update(koodi);
    }

}
