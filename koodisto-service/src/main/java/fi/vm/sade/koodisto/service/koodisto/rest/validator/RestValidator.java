package fi.vm.sade.koodisto.service.koodisto.rest.validator;

public interface RestValidator<T> {
    
    void validateCreateNew(T validatable) throws IllegalArgumentException;
    
    void validateUpdate(T validatable) throws IllegalArgumentException;
    
    void validateDelete(T validatable) throws IllegalArgumentException;
    
    void validateGet(T validatable) throws IllegalArgumentException;

}
