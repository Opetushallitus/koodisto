package fi.vm.sade.koodisto.service.business.changes;

import java.time.LocalDateTime;

public interface ChangesService<T> {
    
    String REMOVED_METADATA_FIELD = "POISTETTU";

    T getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted);
    
    T getChangesDto(String uri, LocalDateTime date, boolean compareToLatestAccepted);

}
