package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto URI is not unique
 * 
 * @author wuoti
 * 
 */
public class KoodiUriNotUniqueException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiUriNotUniqueException.class.getCanonicalName();

    public KoodiUriNotUniqueException() {
        super();
    }

    public KoodiUriNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiUriNotUniqueException(String message) {
        super(message);
    }

    public KoodiUriNotUniqueException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
