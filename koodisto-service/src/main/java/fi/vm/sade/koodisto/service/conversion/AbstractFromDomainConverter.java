package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractFromDomainConverter<FROM extends BaseEntity, TO> implements Converter<FROM, TO> {
}
