package fi.vm.sade.koodisto.service.business.changes;

public interface ChangesDtoService<T> {
    
    public T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);

}
