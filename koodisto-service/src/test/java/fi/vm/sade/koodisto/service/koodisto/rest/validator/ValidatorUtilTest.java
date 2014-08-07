package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.ArrayList;

import org.junit.Test;

import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.model.constraint.exception.KoodistoValidatorRuntimeException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import static org.junit.Assert.assertEquals;


public class ValidatorUtilTest {
    
    private final static String ERROR_MESSAGE = "error"; 
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenGivenStringIsEmpty() {
        ValidatorUtil.checkForBlank("", ERROR_MESSAGE);
    }
    
    @Test    
    public void throwsExceptionWithGivenMessageWhenGivenStringIsNull() {
        try {
            ValidatorUtil.checkForBlank(null, ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
    
    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringIsEmpty() {
        try {
            ValidatorUtil.checkForBlank("", ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
    
    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringHasOnlySpaces() {
        try {
            ValidatorUtil.checkForBlank("    ", ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
    
    @Test
    public void throwsExceptionWithGivenMessageWhenObjectThatIsCheckedIsNull() {
        try {
            ValidatorUtil.checkForNull((Tila) null, ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
    
    @Test(expected = KoodistoNimiEmptyException.class)
    public void throwsGivenExceptionWhenCheckingForBlank() {
        ValidatorUtil.checkForBlank("  ", new KoodistoNimiEmptyException(ERROR_MESSAGE));
    }
    
    @Test(expected = KoodistoValidatorRuntimeException.class)
    public void throwsGivenExceptionWhenCheckingForNull() {
        ValidatorUtil.checkForNull(null, new KoodistoValidatorRuntimeException(ERROR_MESSAGE));
    }
    
    @Test(expected = MetadataEmptyException.class)
    public void throwsExceptionWithNullCollection() {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(null, new MetadataEmptyException(ERROR_MESSAGE));
    }
    
    @Test(expected = MetadataEmptyException.class)
    public void throwsExceptionWithEmptyCollection() {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(new ArrayList<String>(), new MetadataEmptyException(ERROR_MESSAGE));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWithNullInteger() {
        ValidatorUtil.checkForGreaterThan((Integer) null, 1, new IllegalArgumentException());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWithIntegerThatIsNotGreaterThanTheValueGiven() {
        ValidatorUtil.checkForGreaterThan(1, 1, new IllegalArgumentException());
    }
}
