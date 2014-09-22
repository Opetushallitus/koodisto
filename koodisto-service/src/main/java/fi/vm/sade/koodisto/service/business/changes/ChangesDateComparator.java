package fi.vm.sade.koodisto.service.business.changes;

import java.util.Collection;

import org.joda.time.DateTime;

public abstract class ChangesDateComparator<C> {

    public C getClosestMatchingEntity(DateTime dateToCompare, Collection<C> entities) {
        C closestMatching = null;
        for (C entity : entities) {
            closestMatching = compareDateTimes(dateToCompare, entity, closestMatching);
        }
        return closestMatching;
    }

    private C compareDateTimes(DateTime toCompare, C entity, C closestMatching) {
        if (closestMatching == null) {
            return entity;
        }
        DateTime entityDate = getDateFromEntity(entity);
        DateTime closestMatchingDate = getDateFromEntity(closestMatching);
        if (toCompare.isBefore(entityDate) && entityDate.isBefore(closestMatchingDate)
                || toCompare.isAfter(entityDate) && toCompare.isBefore(closestMatchingDate)
                || toCompare.isAfter(entityDate) && entityDate.isAfter(closestMatchingDate)
                || toCompare.isEqual(entityDate)) {
            return entity;
        }
        
        return closestMatching;
    }

    protected abstract DateTime getDateFromEntity(C entity);

}
