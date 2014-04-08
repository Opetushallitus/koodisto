package fi.vm.sade.koodisto.model.constraint.fieldassert;

import java.util.Date;

public class DateIsNullOrNotBeforeAnotherDateAsserter implements Asserter<Date> {
    public DateIsNullOrNotBeforeAnotherDateAsserter() {

    }

    @Override
    public boolean assertTrue(Date first, Date second) {
        return second == null || !second.before(first);
    }
}
