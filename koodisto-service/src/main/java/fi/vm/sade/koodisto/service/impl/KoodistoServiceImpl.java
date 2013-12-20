package fi.vm.sade.koodisto.service.impl;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class KoodistoServiceImpl implements KoodistoService {

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    public List<KoodistoType> searchKoodistos(SearchKoodistosCriteriaType searchCriteria) {
        return conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchCriteria), KoodistoType.class);
    }

    @Override
    public List<KoodistoRyhmaListType> listAllKoodistoRyhmas() {
        return conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListType.class);
    }
}
