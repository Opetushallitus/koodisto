package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.Date;

class DatesChangedHandler {
    
    final Date startDateChanged;
    
    final Date endDateChanged;
    
    final Boolean endDateRemoved;
    
    public DatesChangedHandler(Date startDateChanged, Date endDateChanged, Boolean endDateRemoved) {
        this.startDateChanged = startDateChanged;
        this.endDateChanged = endDateChanged;
        this.endDateRemoved = endDateRemoved;
    }
    
    static DatesChangedHandler setDatesHaveChanged(Date relateToStartDate, Date relateToEndDate, Date latestStartDate, Date latestEndDate) {
        Date startDateChanged = relateToStartDate.equals(latestStartDate) ? null : latestStartDate;
        Date endDateChanged = latestEndDate == null || latestEndDate.equals(relateToEndDate) ? null : latestEndDate;
        Boolean endDateRemoved = relateToEndDate != null && latestEndDate == null ? true : null;
        return new DatesChangedHandler(startDateChanged, endDateChanged, endDateRemoved);
    }
    
    boolean anyChanges() {
        return startDateChanged != null || endDateChanged != null || endDateRemoved != null;
    }
    
}