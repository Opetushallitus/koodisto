package fi.vm.sade.koodisto.service.business.changes;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

public abstract class ChangesDateComparator<C> {

    public C getClosestMatchingEntity(LocalDateTime dateToCompare, Collection<C> entities) {
        C closestMatching = null;
        for (C entity : entities) {
            closestMatching = compareDateTimes(dateToCompare, entity, closestMatching);
        }
        return closestMatching;
    }

    private C compareDateTimes(LocalDateTime toCompare, C entity, C closestMatching) {
        if (closestMatching == null) {
            return entity;
        }
        LocalDateTime entityDate = getDateFromEntity(entity);
        LocalDateTime closestMatchingDate = getDateFromEntity(closestMatching);
        if (toCompare.isBefore(entityDate) && entityDate.isBefore(closestMatchingDate)
                || toCompare.isAfter(entityDate) && toCompare.isBefore(closestMatchingDate)
                || toCompare.isAfter(entityDate) && entityDate.isAfter(closestMatchingDate)
                || toCompare.isEqual(entityDate)) {
            return entity;
        }
        
        return closestMatching;
    }

    protected abstract LocalDateTime getDateFromEntity(C entity);
    protected static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
