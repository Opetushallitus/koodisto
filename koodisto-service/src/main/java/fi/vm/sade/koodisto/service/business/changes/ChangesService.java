package fi.vm.sade.koodisto.service.business.changes;

import java.util.Date;

public interface ChangesService<T> {
    
    public T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);
    
    public T getChangesDto(String uri, Date date, boolean compareToLatestAccepted);

}
