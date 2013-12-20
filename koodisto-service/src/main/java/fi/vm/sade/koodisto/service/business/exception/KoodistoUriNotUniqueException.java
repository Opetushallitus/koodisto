package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto URI is not unique
 * 
 * @author wuoti
 * 
 */
public class KoodistoUriNotUniqueException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoUriNotUniqueException.class.getCanonicalName();

    public KoodistoUriNotUniqueException() {
        super();
    }

    public KoodistoUriNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoUriNotUniqueException(String message) {
        super(message);
    }

    public KoodistoUriNotUniqueException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
