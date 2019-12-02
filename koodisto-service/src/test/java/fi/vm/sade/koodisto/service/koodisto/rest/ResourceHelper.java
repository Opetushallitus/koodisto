package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class ResourceHelper {

    @Autowired
    private CodesResource resource;

    @Autowired
    private CodeElementResource codeElementResource;

    public KoodistoDto createKoodisto(KoodistoDto koodisto) {
        Response response = resource.insert(koodisto);
        assertThat(response.getStatus()).isEqualTo(201);
        return response.readEntity(KoodistoDto.class);
    }

    public KoodistoDto getKoodisto(String koodistoUri, int versio) {
        Response response = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertThat(response.getStatus()).isEqualTo(200);
        return response.readEntity(KoodistoDto.class);
    }

    public KoodistoDto updateKoodisto(KoodistoDto koodisto) {
        Response response = resource.update(koodisto);
        assertThat(response.getStatus()).isEqualTo(201);
        int versio = response.readEntity(Integer.class);
        return getKoodisto(koodisto.getKoodistoUri(), versio);
    }

    public KoodiDto createKoodi(String koodistoUri, KoodiDto koodi) {
        Response response = codeElementResource.insert(koodistoUri, koodi);
        assertThat(response.getStatus()).isEqualTo(201);
        return response.readEntity(KoodiDto.class);
    }

    public ExtendedKoodiDto getKoodi(String koodiUri, int versio) {
        Response response = codeElementResource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertThat(response.getStatus()).isEqualTo(200);
        return response.readEntity(ExtendedKoodiDto.class);
    }

    public KoodiDto updateKoodi(KoodiDto koodi) {
        Response response = codeElementResource.update(koodi);
        assertThat(response.getStatus()).isEqualTo(201);
        return response.readEntity(KoodiDto.class);
    }

}
