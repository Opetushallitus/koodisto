package fi.vm.sade.koodisto.service.conversion;

import fi.vm.sade.koodisto.model.BaseEntity;
import org.springframework.core.convert.converter.Converter;

public abstract class AbstractToDomainConverter<FROM, TO extends BaseEntity> implements Converter<FROM, TO> {

}
