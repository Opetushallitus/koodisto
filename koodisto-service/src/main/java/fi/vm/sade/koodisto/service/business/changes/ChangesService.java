package fi.vm.sade.koodisto.service.business.changes;

import org.joda.time.DateTime;

public interface ChangesService<T> {
    
    String REMOVED_METADATA_FIELD = "POISTETTU";

    T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);
    
    T getChangesDto(String uri, DateTime date, boolean compareToLatestAccepted);

}
