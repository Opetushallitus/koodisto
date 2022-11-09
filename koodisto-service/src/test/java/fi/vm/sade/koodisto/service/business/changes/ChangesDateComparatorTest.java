package fi.vm.sade.koodisto.service.business.changes;

import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;


public class ChangesDateComparatorTest {

    private static final long DATE_23_9_2013 = 1379883600000l;
    private static final long DATE_13_10_2013 = 1381611600000l;
    private static final long DATE_15_11_2013 = 1384466400000l;

    private static final long DATE_BEFORE_FIRST = 1379883500000l;
    private static final long DATE_AFTER_FIRST = 1379883602000l;
    private static final long DATE_AFTER_SECOND = 1384066400000l;
    private static final long DATE_AFTER_THIRD = 1384466490000l;

    private final ChangesDateComparator<TestEntity> comparator = new DummyDateComparator();

    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeChangedDatesInEntities() {
        assertEquals(1, comparator.getClosestMatchingEntity(dateFromLong(DATE_BEFORE_FIRST), givenEntities()).version);
    }

    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeSecondVersion() {
        assertEquals(1, comparator.getClosestMatchingEntity(dateFromLong(DATE_AFTER_FIRST), givenEntities()).version);
    }

    @Test
    public void returnsSecondVersionWhenDateGivenIsAfterSecondButBeforeThirdVersion() {
        assertEquals(2, comparator.getClosestMatchingEntity(dateFromLong(DATE_AFTER_SECOND), givenEntities()).version);
    }

    @Test
    public void returnsThirdVersionWhenDateGivenIsAfterThirdVersion() {
        assertEquals(3, comparator.getClosestMatchingEntity(dateFromLong(DATE_AFTER_THIRD), givenEntities()).version);
    }

    @Test
    public void returnsSecondVersionWhenDateMatchesItExactly() {
        assertEquals(2, comparator.getClosestMatchingEntity(dateFromLong(DATE_13_10_2013), givenEntities()).version);
    }

    @Test
    public void returnsSecondEntityUsingDatesInSameMonth() {
        assertEquals(2, comparator.getClosestMatchingEntity(convertToDate(2014, 9, 21, 12, 0, 0), givenEntitiesAllChangedDuringSameMonth(9)).version);
    }


    private List<TestEntity> givenEntities() {
        return Arrays.asList(new TestEntity(1, dateFromLong(DATE_23_9_2013)),
                new TestEntity(2, dateFromLong(DATE_13_10_2013)),
                new TestEntity(3, dateFromLong(DATE_15_11_2013)));
    }

    private List<TestEntity> givenEntitiesAllChangedDuringSameMonth(int month) {
        return Arrays.asList(new TestEntity(1, convertToDate(2014, month, 19, 12, 0, 0)),
                new TestEntity(2, convertToDate(2014, month, 21, 5, 0, 0)),
                new TestEntity(3, convertToDate(2014, month, 22, 12, 0, 0)));
    }

    public static LocalDateTime convertToDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }

    private class TestEntity {

        public final LocalDateTime changedDate;

        public final int version;

        public TestEntity(int version, LocalDateTime changedDate) {
            this.version = version;
            this.changedDate = changedDate;
        }
    }

    private class DummyDateComparator extends ChangesDateComparator<TestEntity> {

        @Override
        protected LocalDateTime getDateFromEntity(TestEntity entity) {
            return entity.changedDate;
        }

    }

    private LocalDateTime dateFromLong(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId());
    }
}
