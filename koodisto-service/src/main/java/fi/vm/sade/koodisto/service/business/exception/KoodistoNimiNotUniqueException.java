package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where the nimi set for koodisto is not unique
 *
 * @author wuoti
 *
 */
public class KoodistoNimiNotUniqueException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoNimiNotUniqueException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.name.not.unique";

    public KoodistoNimiNotUniqueException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoNimiNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNimiNotUniqueException(String message) {
        super(message);
    }

    public KoodistoNimiNotUniqueException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
