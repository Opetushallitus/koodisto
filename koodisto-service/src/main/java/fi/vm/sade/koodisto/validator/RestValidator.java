package fi.vm.sade.koodisto.validator;

public interface RestValidator<T> {
    
    void validate(T validatable, ValidationType type);

    void validateInsert(T validatable);
    
    void validateUpdate(T validatable);

}
