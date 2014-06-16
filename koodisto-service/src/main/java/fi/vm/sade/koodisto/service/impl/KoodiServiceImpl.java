package fi.vm.sade.koodisto.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;

/**
 * Implementation for KoodiService interface
 * 
 * @author Eetu Blomqvist
 * 
 */
public class KoodiServiceImpl implements KoodiService {

    private static Logger log = LoggerFactory.getLogger(KoodiServiceImpl.class);

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    public List<KoodiType> listKoodiByRelation(KoodiUriAndVersioType koodi, boolean onAlaKoodi,
            SuhteenTyyppiType suhdeTyyppi) throws GenericFault {
        log.info("listKoodiByRelation called with parameters koodiUri=" + koodi.getKoodiUri() + ", versio="
                + koodi.getVersio() + ", isChild=" + onAlaKoodi + " relationType=" + suhdeTyyppi);
        SuhteenTyyppi st = SuhteenTyyppi.valueOf(suhdeTyyppi.name());
        return conversionService.convertAll(koodiBusinessService.listByRelation(koodi, st, onAlaKoodi), KoodiType.class);
    }

    @Override
    public List<KoodiType> searchKoodisByKoodisto(SearchKoodisByKoodistoCriteriaType searchCriteria) {
        return conversionService.convertAll(koodiBusinessService.searchKoodis(searchCriteria), KoodiType.class);

    }

    @Override
    public List<KoodiType> searchKoodis(SearchKoodisCriteriaType searchCriteria) {
        return conversionService.convertAll(koodiBusinessService.searchKoodis(searchCriteria), KoodiType.class);

    }
}
