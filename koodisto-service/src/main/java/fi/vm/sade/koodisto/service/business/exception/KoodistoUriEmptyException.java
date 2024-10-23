package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodisto URI is empty or null
 *
 * @author wuoti
 *
 */
public class KoodistoUriEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoUriEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.uri.empty";

    public KoodistoUriEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoUriEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoUriEmptyException(String message) {
        super(message);
    }

    public KoodistoUriEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
