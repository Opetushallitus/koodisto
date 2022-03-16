package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.resource.CodeElementResource;
import fi.vm.sade.koodisto.resource.CodesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

;import static org.junit.Assert.assertEquals;

@Component
public class ResourceHelper {

    @Autowired
    private CodesResource resource;

    @Autowired
    private CodeElementResource codeElementResource;

    public KoodistoDto createKoodisto(KoodistoDto koodisto) {
        ResponseEntity response = resource.insert(koodisto);
        assertEquals(response.getStatusCodeValue(), 201);
        return (KoodistoDto) response.getBody();
    }

    public KoodistoDto getKoodisto(String koodistoUri, int versio) {
        ResponseEntity response = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertEquals(response.getStatusCodeValue(), 200);
        return (KoodistoDto) response.getBody();
    }

    public KoodistoDto updateKoodisto(KoodistoDto koodisto) {
        ResponseEntity response = resource.update(koodisto);
        assertEquals(response.getStatusCodeValue(), 201);
        int versio = (int) response.getBody();
        return getKoodisto(koodisto.getKoodistoUri(), versio);
    }

    public KoodiDto createKoodi(String koodistoUri, KoodiDto koodi) {
        ResponseEntity response = codeElementResource.insert(koodistoUri, koodi);
        assertEquals(response.getStatusCodeValue(), 201);
        return (KoodiDto) response.getBody();
    }

    public ExtendedKoodiDto getKoodi(String koodiUri, int versio) {
        ResponseEntity response = codeElementResource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertEquals(response.getStatusCodeValue(), 200);
        return (ExtendedKoodiDto) response.getBody();
    }

    public KoodiDto updateKoodi(KoodiDto koodi) {
        ResponseEntity response = codeElementResource.update(koodi);
        assertEquals(response.getStatusCodeValue(), 201);
        return (KoodiDto) response.getBody();
    }

}
