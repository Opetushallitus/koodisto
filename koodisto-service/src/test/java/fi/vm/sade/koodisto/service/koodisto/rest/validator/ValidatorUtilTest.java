package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import org.junit.Test;

import fi.vm.sade.koodisto.model.Tila;
import static org.junit.Assert.assertEquals;


public class ValidatorUtilTest {
    
    private final static String ERROR_MESSAGE = "error"; 
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenGivenStringIsEmpty() {
        ValidatorUtil.checkForNullOrEmpty("", ERROR_MESSAGE);
    }
    
    @Test    
    public void throwsExceptionWithGivenMessageWhenGivenStringIsNull() {
        try {
            ValidatorUtil.checkForNullOrEmpty(null, ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
    
    @Test
    public void throwsExceptionWithGivenMessageWhenGivenStringIsEmpty() {
        try {
            ValidatorUtil.checkForNullOrEmpty("", ERROR_MESSAGE);
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
}
