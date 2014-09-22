package fi.vm.sade.koodisto.service.business.changes;

import org.joda.time.DateTime;

public interface ChangesService<T> {
    
    public T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);
    
    public T getChangesDto(String uri, DateTime date, boolean compareToLatestAccepted);

}
