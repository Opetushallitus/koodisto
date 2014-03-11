package fi.vm.sade.koodisto.service.business;


import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;

public interface KoodistoRyhmaBusinessService {
    KoodistoRyhma createKoodistoRyhma(KoodistoRyhmaDto koodistoRyhmaDto);
    KoodistoRyhma updateKoodistoRyhma(KoodistoRyhmaDto koodistoRyhmaDto);
    KoodistoRyhma getKoodistoRyhmaById(Long id);
    void delete(Long id);
}
