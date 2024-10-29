package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodi URI is empty or null
 *
 * @author wuoti
 *
 */
public class KoodiUriEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiUriEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.uri.empty";

    public KoodiUriEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodiUriEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiUriEmptyException(String message) {
        super(message);
    }

    public KoodiUriEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
