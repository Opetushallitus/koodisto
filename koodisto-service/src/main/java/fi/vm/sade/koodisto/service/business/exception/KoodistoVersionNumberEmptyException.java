package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto version number is empty
 * 
 * @author wuoti
 * 
 */
public class KoodistoVersionNumberEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoVersionNumberEmptyException.class.getCanonicalName();

    public KoodistoVersionNumberEmptyException() {
        super();
    }

    public KoodistoVersionNumberEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoVersionNumberEmptyException(String message) {
        super(message);
    }

    public KoodistoVersionNumberEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
