/**
 *
 */
package fi.vm.sade.koodisto.service.impl;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiAdminService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.log.KoodistoAuditLogger;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

/**
 * Service interface implementation for KoodiAdminService
 *
 * @author Eetu Blomqvist
 *
 */
public class KoodiAdminServiceImpl implements KoodiAdminService {

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private KoodistoAuditLogger koodistoAuditLogger;

    @Override
    @Secured({KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public KoodiType updateKoodi(UpdateKoodiDataType updateKoodiData) {
        KoodiType updated = conversionService.convert(koodiBusinessService.updateKoodi(updateKoodiData), KoodiType.class);
        koodistoAuditLogger.logUpdateKoodi(updateKoodiData);
        return updated;
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public KoodiBusinessService getKoodiService() {
        return koodiBusinessService;
    }

    public void setKoodiService(KoodiBusinessService koodiService) {
        this.koodiBusinessService = koodiService;
    }

    @Override
    @Secured({KoodistoRole.CRUD})
    public void massCreate(String koodistoUri, List<UpdateKoodiDataType> koodiList) {
        koodiBusinessService.massCreate(koodistoUri, koodiList);
        koodistoAuditLogger.logMassCreate(koodistoUri, koodiList);
    }

    @Override
    @Secured({KoodistoRole.CRUD})
    public void deleteKoodiVersion(String koodiUri, int koodiVersio) {
        koodiBusinessService.delete(koodiUri, koodiVersio);
        koodistoAuditLogger.logDeleteKoodiVersion(koodiUri, koodiVersio);
    }

    @Override
    @Secured({KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public void addRelation(String ylaKoodi, String alaKoodi,
                            SuhteenTyyppiType suhteenTyyppi) throws GenericFault {
        fi.vm.sade.koodisto.model.SuhteenTyyppi st = fi.vm.sade.koodisto.model.SuhteenTyyppi.valueOf(suhteenTyyppi
                .name());
        koodiBusinessService.addRelation(ylaKoodi, alaKoodi, st);
        koodistoAuditLogger.logAddRelation(ylaKoodi, alaKoodi, suhteenTyyppi);
    }

    @Override
    @Secured({KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public void addRelationByAlakoodi(String ylaKoodi, List<String> alaKoodis,
                                      SuhteenTyyppiType suhteenTyyppi) throws GenericFault {
        fi.vm.sade.koodisto.model.SuhteenTyyppi st = fi.vm.sade.koodisto.model.SuhteenTyyppi.valueOf(suhteenTyyppi
                .name());
        koodiBusinessService.addRelation(ylaKoodi, alaKoodis, st);
        koodistoAuditLogger.logAddRelationByAlakoodi(ylaKoodi, alaKoodis, suhteenTyyppi);
    }

    @Override
    @Secured({KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public void removeRelationByAlakoodi(String ylaKoodi, List<String> alaKoodis,
                                         SuhteenTyyppiType suhteenTyyppi) throws GenericFault {
        fi.vm.sade.koodisto.model.SuhteenTyyppi st = fi.vm.sade.koodisto.model.SuhteenTyyppi.valueOf(suhteenTyyppi
                .name());
        koodiBusinessService.removeRelation(ylaKoodi, alaKoodis, st);
        koodistoAuditLogger.logRemoveRelationByAlakoodi(ylaKoodi, alaKoodis, st);
    }

    @Override
    @Secured({KoodistoRole.CRUD})
    public KoodiType createKoodi(String koodistoUri, CreateKoodiDataType createKoodiData) {
        KoodiType koodi = conversionService.convert(koodiBusinessService.createKoodi(koodistoUri, createKoodiData),
                KoodiType.class);
        koodistoAuditLogger.logCreateKoodi(koodistoUri, createKoodiData);
        return koodi;
    }

}
