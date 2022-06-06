package fi.vm.sade.koodisto.service.business.changes;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ChangesDateComparatorTest {

    private static long DATE_23_9_2013 = 1379883600000l, DATE_13_10_2013 = 1381611600000l, DATE_15_11_2013 = 1384466400000l;
    
    private static long DATE_BEFORE_FIRST = 1379883500000l, DATE_AFTER_FIRST = 1379883602000l, DATE_AFTER_SECOND = 1384066400000l;
    private static long DATE_AFTER_THIRD = 1384466490000l;
    
    private ChangesDateComparator<TestEntity> comparator = new DummyDateComparator();
    
    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeChangedDatesInEntities() {
        assertEquals(1, comparator.getClosestMatchingEntity(new DateTime(DATE_BEFORE_FIRST), givenEntities()).version);
    }
    
    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeSecondVersion() {
        assertEquals(1, comparator.getClosestMatchingEntity(new DateTime(DATE_AFTER_FIRST), givenEntities()).version);
    }
    
    @Test
    public void returnsSecondVersionWhenDateGivenIsAfterSecondButBeforeThirdVersion() {
        assertEquals(2, comparator.getClosestMatchingEntity(new DateTime(DATE_AFTER_SECOND), givenEntities()).version);
    }
    
    @Test
    public void returnsThirdVersionWhenDateGivenIsAfterThirdVersion() {
        assertEquals(3, comparator.getClosestMatchingEntity(new DateTime(DATE_AFTER_THIRD), givenEntities()).version);
    }
    
    @Test
    public void returnsSecondVersionWhenDateMatchesItExactly() {
        assertEquals(2, comparator.getClosestMatchingEntity(new DateTime(DATE_13_10_2013), givenEntities()).version);
    }
    
    @Test
    public void returnsSecondEntityUsingDatesInSameMonth() {
        assertEquals(2, comparator.getClosestMatchingEntity(convertToDate(2014, 9, 21, 12, 0, 0), givenEntitiesAllChangedDuringSameMonth(9)).version);
    }
    
    
    private List<TestEntity> givenEntities() {
        return Arrays.asList(new TestEntity(1, new DateTime(DATE_23_9_2013)),
                new TestEntity(2, new DateTime(DATE_13_10_2013)),
                new TestEntity(3, new DateTime(DATE_15_11_2013)));
    }
    
    private List<TestEntity> givenEntitiesAllChangedDuringSameMonth(int month) {
        return Arrays.asList(new TestEntity(1, convertToDate(2014, month, 19, 12,0,0)),
                new TestEntity(2, convertToDate(2014, month, 21, 5,0,0)),
                new TestEntity(3, convertToDate(2014, month, 22, 12,0,0)));
    }
    
    public static DateTime convertToDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return new DateTime(year,  month, dayOfMonth, hour, minute, second);
    }
    
    private class TestEntity {
        
        public final DateTime changedDate;
        
        public final int version;
        
        public TestEntity(int version, DateTime changedDate) {
            this.version = version;
            this.changedDate = changedDate;
        }
    }
    
    private class DummyDateComparator extends ChangesDateComparator<TestEntity> {

        @Override
        protected DateTime getDateFromEntity(TestEntity entity) {
            return new DateTime(entity.changedDate);
        }

    }
}
