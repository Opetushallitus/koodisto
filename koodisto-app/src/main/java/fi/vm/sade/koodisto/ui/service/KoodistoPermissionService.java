package fi.vm.sade.koodisto.ui.service;

import fi.vm.sade.generic.service.PermissionService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;

public interface KoodistoPermissionService extends PermissionService {

    boolean userCanAddKoodisto();

    boolean userCanDeleteKoodisto(final KoodistoType koodisto);

    boolean userCanEditKoodisto(final KoodistoType koodisto);

    boolean userCanAddKoodiToKoodisto(final KoodistoType koodisto);

    boolean userCanEditKoodi(final KoodiType koodi);

    boolean userCanDeleteKoodi(final KoodiType koodi);
}
