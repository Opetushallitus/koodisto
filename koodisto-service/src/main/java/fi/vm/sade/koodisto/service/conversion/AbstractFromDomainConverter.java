package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;
import org.springframework.stereotype.Component;

@Component
public interface AbstractFromDomainConverter<FROM extends BaseEntity, TO> extends ExtendedConverter<FROM, TO> {
}
