package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;

public interface AbstractToDomainConverter<S, T extends BaseEntity> extends ExtendedConverter<S, T> {

}
