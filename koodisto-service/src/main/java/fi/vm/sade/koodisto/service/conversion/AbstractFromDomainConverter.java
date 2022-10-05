package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;
import org.springframework.stereotype.Component;

@Component
public interface AbstractFromDomainConverter<S extends BaseEntity, T> extends ExtendedConverter<S, T> {
}
