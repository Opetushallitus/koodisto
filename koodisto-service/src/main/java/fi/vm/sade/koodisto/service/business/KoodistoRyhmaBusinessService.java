package fi.vm.sade.koodisto.service.business;


import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;

public interface KoodistoRyhmaBusinessService {
    KoodistoRyhma createKoodistoRyhma(KoodistoRyhmaDto koodistoRyhmaDto);
}
