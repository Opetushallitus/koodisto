package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;

public interface RestValidator<T> {
    
    void validate(T validatable, ValidationType type);

    void validateInsert(T validatable);
    
    void validateUpdate(T validatable);

}
