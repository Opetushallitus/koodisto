package fi.vm.sade.koodisto.service.business.exception;

public class KoodiVersioHasRelationsException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiVersioHasRelationsException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.has.relations";

    public KoodiVersioHasRelationsException() {
        super(ERROR_MESSAGE);
    }

    public KoodiVersioHasRelationsException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiVersioHasRelationsException(String message) {
        super(message);
    }

    public KoodiVersioHasRelationsException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
