package fi.vm.sade.koodisto.service.business;


import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.internal.InternalInsertKoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;

import java.util.List;

public interface KoodistoRyhmaBusinessService {
    KoodistoRyhma createKoodistoRyhma(KoodistoRyhmaDto koodistoRyhmaDto);
    KoodistoRyhma createKoodistoRyhma(InternalInsertKoodistoRyhmaDto koodistoRyhma);
    KoodistoRyhma updateKoodistoRyhma(KoodistoRyhmaDto koodistoRyhmaDto);
    KoodistoRyhma updateKoodistoRyhma(String koodistoRyhmaUri, InternalInsertKoodistoRyhmaDto updateKoodistoRyhma);
    KoodistoRyhma getKoodistoRyhmaById(Long id);
    List<KoodistoRyhma> getEmptyKoodistoRyhma();
    void delete(Long id);
    void delete(String uri);
}
