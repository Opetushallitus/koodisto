package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.validator.ValidationType;

public interface RestValidator<T> {
    
    void validate(T validatable, ValidationType type);

    void validateInsert(T validatable);
    
    void validateUpdate(T validatable);

}
