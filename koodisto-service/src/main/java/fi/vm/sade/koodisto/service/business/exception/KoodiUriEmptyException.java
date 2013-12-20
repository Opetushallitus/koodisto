package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi URI is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodiUriEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiUriEmptyException.class.getCanonicalName();

    public KoodiUriEmptyException() {
        super();
    }

    public KoodiUriEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiUriEmptyException(String message) {
        super(message);
    }

    public KoodiUriEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
