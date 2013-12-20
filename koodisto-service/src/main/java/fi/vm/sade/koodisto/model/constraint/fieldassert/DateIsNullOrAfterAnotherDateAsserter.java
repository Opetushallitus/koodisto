package fi.vm.sade.koodisto.model.constraint.fieldassert;

import java.util.Date;

public class DateIsNullOrAfterAnotherDateAsserter implements Asserter<Date> {
    public DateIsNullOrAfterAnotherDateAsserter() {

    }

    @Override
    public boolean assertTrue(Date first, Date second) {
        return second == null || second.after(first);
    }
}
