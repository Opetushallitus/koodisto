package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import software.amazon.awssdk.http.HttpStatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertEquals;

@Component
public class ResourceHelper {

    @Autowired
    private CodesResource resource;

    @Autowired
    private CodeElementResource codeElementResource;

    public KoodistoDto createKoodisto(KoodistoDto koodisto) {
        ResponseEntity<Object> response = resource.insert(koodisto);
        assertEquals(HttpStatusCode.CREATED, response.getStatusCode());
        return (KoodistoDto) response.getBody();
    }

    public KoodistoDto getKoodisto(String koodistoUri, int versio) {
        ResponseEntity<Object> response = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertEquals(HttpStatusCode.OK, response.getStatusCode());
        return (KoodistoDto) response.getBody();
    }

    public KoodistoDto updateKoodisto(KoodistoDto koodisto) {
        ResponseEntity<Object> response = resource.update(koodisto);
        assertEquals(HttpStatusCode.CREATED, response.getStatusCode());
        int versio = (int) response.getBody();
        return getKoodisto(koodisto.getKoodistoUri(), versio);
    }

    public KoodiDto createKoodi(String koodistoUri, KoodiDto koodi) {
        ResponseEntity<Object> response = codeElementResource.insert(koodistoUri, koodi);
        assertEquals(HttpStatusCode.CREATED, response.getStatusCode());
        return (KoodiDto) response.getBody();
    }

    public ExtendedKoodiDto getKoodi(String koodiUri, int versio) {
        ResponseEntity<Object> response = codeElementResource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertEquals(HttpStatusCode.OK, response.getStatusCode());
        return (ExtendedKoodiDto) response.getBody();
    }

    public KoodiDto updateKoodi(KoodiDto koodi) {
        ResponseEntity<Object> response = codeElementResource.update(koodi);
        assertEquals(HttpStatusCode.CREATED, response.getStatusCode());
        return (KoodiDto) response.getBody();
    }
}
