package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi version number is empty
 * 
 * @author wuoti
 * 
 */
public class KoodiVersionNumberEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiVersionNumberEmptyException.class.getCanonicalName();

    public KoodiVersionNumberEmptyException() {
        super();
    }

    public KoodiVersionNumberEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiVersionNumberEmptyException(String message) {
        super(message);
    }

    public KoodiVersionNumberEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
