package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where the koodi could not be found
 *
 * @author wuoti
 *
 */
public class KoodiNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNotFoundException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.koodi.not.found";

    public KoodiNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public KoodiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNotFoundException(String message) {
        super(message);
    }

    public KoodiNotFoundException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
