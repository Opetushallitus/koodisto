package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodi does not belong to the specified koodisto
 *
 * @author wuoti
 *
 */
public class KoodiNotInKoodistoException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNotInKoodistoException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.not.in.codes";

    public KoodiNotInKoodistoException() {
        super(ERROR_MESSAGE);
    }

    public KoodiNotInKoodistoException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNotInKoodistoException(String message) {
        super(message);
    }

    public KoodiNotInKoodistoException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
