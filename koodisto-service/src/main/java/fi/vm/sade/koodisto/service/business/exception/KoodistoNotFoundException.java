package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodisto could not be found
 *
 * @author wuoti
 *
 */
public class KoodistoNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoNotFoundException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.koodisto.not.found";

    public KoodistoNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNotFoundException(String message) {
        super(message);
    }

    public KoodistoNotFoundException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
