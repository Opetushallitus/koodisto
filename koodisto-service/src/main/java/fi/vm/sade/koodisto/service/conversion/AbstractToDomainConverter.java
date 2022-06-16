package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;

public abstract class AbstractToDomainConverter<FROM, TO extends BaseEntity> implements ExtendedConverter<FROM, TO> {

}
