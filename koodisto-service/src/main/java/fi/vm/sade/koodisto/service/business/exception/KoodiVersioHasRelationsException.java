package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodiVersioHasRelationsException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiVersioHasRelationsException.class.getCanonicalName();

    public KoodiVersioHasRelationsException() {
        super();
    }

    public KoodiVersioHasRelationsException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiVersioHasRelationsException(String message) {
        super(message);
    }

    public KoodiVersioHasRelationsException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
