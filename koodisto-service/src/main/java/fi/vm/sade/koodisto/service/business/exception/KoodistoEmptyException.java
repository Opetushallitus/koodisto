package fi.vm.sade.koodisto.service.business.exception;

public class KoodistoEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.has.no.codeelements";

    public KoodistoEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoEmptyException(String message) {
        super(message);
    }

    public KoodistoEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
