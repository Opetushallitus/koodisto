package fi.vm.sade.koodisto.service.business.exception;

public class KoodistoVersioNotPassiivinenException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoVersioNotPassiivinenException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.not.passive";

    public KoodistoVersioNotPassiivinenException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoVersioNotPassiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoVersioNotPassiivinenException(String message) {
        super(message);
    }

    public KoodistoVersioNotPassiivinenException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
