package fi.vm.sade.koodisto.service.koodisto.rest.validator;

public interface RestValidator<T> {
    
    void validateInsert(T validatable);
    
    void validateUpdate(T validatable);
    
    void validateDelete(String uri, Integer version);
    
    void validateGet(String uri);

}
