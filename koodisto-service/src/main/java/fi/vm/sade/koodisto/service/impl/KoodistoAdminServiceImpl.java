/**
 * 
 */
package fi.vm.sade.koodisto.service.impl;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

/**
 * @author tommiha
 * 
 */
public class KoodistoAdminServiceImpl implements KoodistoAdminService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    @Secured({KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public KoodistoType updateKoodisto(UpdateKoodistoDataType updateKoodistoData) {
        return conversionService.convert(koodistoBusinessService.updateKoodisto(updateKoodistoData), KoodistoType.class);
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public KoodistoBusinessService getKoodistoService() {
        return koodistoBusinessService;
    }

    public void setKoodistoService(KoodistoBusinessService koodistoService) {
        this.koodistoBusinessService = koodistoService;
    }

    @Override
    @Secured({KoodistoRole.CRUD})
    public void deleteKoodistoVersion(String koodistoUri, int koodistoVersio) {
        koodistoBusinessService.delete(koodistoUri, koodistoVersio);
    }

    @Override
    @Secured({KoodistoRole.CRUD})
    public KoodistoType createKoodisto(List<String> koodistoRyhmaUris, CreateKoodistoDataType createKoodistoData) {
        return conversionService.convert(koodistoBusinessService.createKoodisto(koodistoRyhmaUris, createKoodistoData), KoodistoType.class);
    }

}
