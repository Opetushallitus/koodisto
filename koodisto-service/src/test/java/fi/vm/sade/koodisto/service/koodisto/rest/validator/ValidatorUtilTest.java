package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.model.constraint.exception.KoodistoValidatorRuntimeException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ValidatorUtilTest {

    private static final KoodistoValidationException ERROR = new KoodistoValidationException("error");

    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenGivenStringIsEmpty() {
        ValidatorUtil.checkForBlank("", ERROR);
    }

    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringIsNull() {
        try {
            ValidatorUtil.checkForBlank(null, ERROR);
        } catch (KoodistoValidationException e) {
            assertEquals(ERROR.getMessage(), e.getMessage());
        }
    }

    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringIsEmpty() {
        try {
            ValidatorUtil.checkForBlank("", ERROR);
        } catch (KoodistoValidationException e) {
            assertEquals(ERROR.getMessage(), e.getMessage());
        }
    }

    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringHasOnlySpaces() {
        try {
            ValidatorUtil.checkForBlank("    ", ERROR);
        } catch (KoodistoValidationException e) {
            assertEquals(ERROR.getMessage(), e.getMessage());
        }
    }

    @Test
    public void throwsExceptionWithGivenMessageWhenObjectThatIsCheckedIsNull() {
        try {
            ValidatorUtil.checkForNull((Tila) null, ERROR);
        } catch (KoodistoValidationException e) {
            assertEquals(ERROR.getMessage(), e.getMessage());
        }
    }

    @Test
    public void throwsExceptionWithGivenMessageWhenStartDateIsAfterEndDate() {
        try {
            ValidatorUtil.checkBeginDateBeforeEndDate(new Date(), new Date(0L), ERROR);
        } catch (KoodistoValidationException e) {
            assertEquals(ERROR.getMessage(), e.getMessage());
        }
    }

    @Test(expected = KoodistoNimiEmptyException.class)
    public void throwsGivenExceptionWhenCheckingForBlank() {
        ValidatorUtil.checkForBlank("  ", new KoodistoNimiEmptyException(ERROR));
    }

    @Test(expected = KoodistoValidatorRuntimeException.class)
    public void throwsGivenExceptionWhenCheckingForNull() {
        ValidatorUtil.checkForNull(null, new KoodistoValidatorRuntimeException(ERROR));
    }

    @Test(expected = MetadataEmptyException.class)
    public void throwsExceptionWithNullCollection() {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(null, new MetadataEmptyException(ERROR));
    }

    @Test(expected = MetadataEmptyException.class)
    public void throwsExceptionWithEmptyCollection() {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(new ArrayList<String>(), new MetadataEmptyException(ERROR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWithNullInteger() {
        ValidatorUtil.checkForGreaterThan((Integer) null, 1, new IllegalArgumentException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWithIntegerThatIsNotGreaterThanTheValueGiven() {
        ValidatorUtil.checkForGreaterThan(1, 1, new IllegalArgumentException());
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenDayOfMonthIsLessThanOne() {
        ValidatorUtil.validateDateParameters(0, 1, 2000, 0, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenDayOfMonthIsGreaterThan31() {
        ValidatorUtil.validateDateParameters(32, 1, 2000, 0, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenMonthIsLessThanOne() {
        ValidatorUtil.validateDateParameters(1, 0, 2000, 0, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenDayOfMonthIsOverTwelve() {
        ValidatorUtil.validateDateParameters(2, 13, 2000, 0, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenYearIsNegative() {
        ValidatorUtil.validateDateParameters(1, 1, -2000, 0, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenHourOfDayIsNegative() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, -1, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenHourOfDayIsOver23() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, 24, 0, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenMinuteIsNegative() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, 0, -1, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenMinuteIsOver59() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, 0, 60, 0);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenSecondIsNegative() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, 0, 0, -1);
    }
    
    @Test(expected = KoodistoValidationException.class)
    public void throwsExceptionWhenSecondIsOver59() {
        ValidatorUtil.validateDateParameters(1, 1, 2000, 0, 0, 60);
    }
    
    @Test
    public void passesWithCorrectDateParameters() {
        ValidatorUtil.validateDateParameters(1, 1, 2010, 0, 0, 0);
        ValidatorUtil.validateDateParameters(31, 12, 2010, 23, 59, 59);
    }
}
