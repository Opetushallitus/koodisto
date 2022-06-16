package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractFromDomainConverter<FROM extends BaseEntity, TO> implements ExtendedConverter<FROM, TO> {
}
