package fi.vm.sade.koodisto.ui.service;

import org.springframework.stereotype.Service;

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;

/**
 * 
 * @author wuoti
 * 
 */
@Service
public class KoodistoPermissionServiceImpl extends AbstractPermissionService implements KoodistoPermissionService {

    protected KoodistoPermissionServiceImpl() {
        super("KOODISTO");
    }

    @Override
    public boolean userCanAddKoodisto() {
        return userCanCreateReadUpdateAndDelete();
    }

    @Override
    public boolean userCanDeleteKoodisto(final KoodistoType koodisto) {
        return checkAccess(koodisto.getOrganisaatioOid(), ROLE_CRUD);
    }

    @Override
    public boolean userCanEditKoodisto(final KoodistoType koodisto) {
        return checkAccess(koodisto.getOrganisaatioOid(), ROLE_CRUD, ROLE_RU);
    }

    @Override
    public boolean userCanAddKoodiToKoodisto(final KoodistoType koodisto) {
        return checkAccess(koodisto.getOrganisaatioOid(), ROLE_CRUD);
    }

    @Override
    public boolean userCanEditKoodi(KoodiType koodi) {
        return checkAccess(koodi.getKoodisto().getOrganisaatioOid(), ROLE_CRUD, ROLE_RU);
    }

    @Override
    public boolean userCanDeleteKoodi(KoodiType koodi) {
        return checkAccess(koodi.getKoodisto().getOrganisaatioOid(), ROLE_CRUD);
    }

}
