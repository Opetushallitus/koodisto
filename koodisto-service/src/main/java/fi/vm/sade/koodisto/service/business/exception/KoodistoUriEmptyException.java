package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto URI is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoUriEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoUriEmptyException.class.getCanonicalName();

    public KoodistoUriEmptyException() {
        super();
    }

    public KoodistoUriEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoUriEmptyException(String message) {
        super(message);
    }

    public KoodistoUriEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
