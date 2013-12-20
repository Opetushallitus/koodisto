package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto could not be found
 * 
 * @author wuoti
 * 
 */
public class KoodistoNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoNotFoundException.class.getCanonicalName();

    public KoodistoNotFoundException() {
        super();
    }

    public KoodistoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNotFoundException(String message) {
        super(message);
    }

    public KoodistoNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
