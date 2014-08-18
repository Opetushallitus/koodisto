package fi.vm.sade.koodisto.service.business.changes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ChangesDateComparatorTest {
    
    private static long DATE_23_9_2013 = 1379883600000l, DATE_13_10_2013 = 1381611600000l, DATE_15_11_2013 = 1384466400000l;
    
    private static long DATE_BEFORE_FIRST = 1379883500000l, DATE_AFTER_FIRST = 1379883602000l, DATE_AFTER_SECOND = 1384066400000l;
    private static long DATE_AFTER_THIRD = 1384466490000l;
    
    private ChangesDateComparator<TestEntity> comparator = new DummyDateComparator();
    
    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeChangedDatesInEntities() {
        assertEquals(1, comparator.getClosestMatchingEntity(new Date(DATE_BEFORE_FIRST), givenEntities()).version);
    }
    
    @Test
    public void returnsFirstVersionWhenDateGivenIsBeforeSecondVersion() {
        assertEquals(1, comparator.getClosestMatchingEntity(new Date(DATE_AFTER_FIRST), givenEntities()).version);
    }
    
    @Test
    public void returnsSecondVersionWhenDateGivenIsAfterSecondButBeforeThirdVersion() {
        assertEquals(2, comparator.getClosestMatchingEntity(new Date(DATE_AFTER_SECOND), givenEntities()).version);
    }
    
    @Test
    public void returnsThirdVersionWhenDateGivenIsAfterThirdVersion() {
        assertEquals(3, comparator.getClosestMatchingEntity(new Date(DATE_AFTER_THIRD), givenEntities()).version);
    }
    
    
    private List<TestEntity> givenEntities() {
        return Arrays.asList(new TestEntity(1, new Date(DATE_23_9_2013)),
                new TestEntity(2, new Date(DATE_13_10_2013)),
                new TestEntity(3, new Date(DATE_15_11_2013)));
    }
    
    private class TestEntity {
        
        public final Date changedDate;
        
        public final int version;
        
        public TestEntity(int version, Date changedDate) {
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
