package fi.vm.sade.koodisto.service.business.changes;

import org.joda.time.DateTime;

public interface ChangesService<T> {
    
    public static final String REMOVED_METADATA_FIELD = "POISTETTU";

    public T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);
    
    public T getChangesDto(String uri, DateTime date, boolean compareToLatestAccepted);

}
