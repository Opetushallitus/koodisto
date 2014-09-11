package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi value is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodiArvoEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiArvoEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.value.empty";

    public KoodiArvoEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodiArvoEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiArvoEmptyException(String message) {
        super(message);
    }

    public KoodiArvoEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
