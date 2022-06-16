package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;

public interface AbstractToDomainConverter<FROM, TO extends BaseEntity> extends ExtendedConverter<FROM, TO> {

}
