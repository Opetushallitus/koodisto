package fi.vm.sade.koodisto.service.koodisto.rest.validator;

public interface RestValidator<T> {
    
    void validateCreateNew(T validatable);
    
    void validateUpdate(T validatable);
    
    void validateDelete(T validatable);
    
    void validateGet(T validatable);

}
