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
    private static final String ERROR_MESSAGE = "error.codes.version.empty";

    public KoodistoVersionNumberEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoVersionNumberEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoVersionNumberEmptyException(String message) {
        super(message);
    }

    public KoodistoVersionNumberEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
