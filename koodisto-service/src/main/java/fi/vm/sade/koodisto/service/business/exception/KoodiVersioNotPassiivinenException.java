package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodiVersioNotPassiivinenException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiVersioNotPassiivinenException.class.getCanonicalName();

    public KoodiVersioNotPassiivinenException() {
        super();
    }

    public KoodiVersioNotPassiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiVersioNotPassiivinenException(String message) {
        super(message);
    }

    public KoodiVersioNotPassiivinenException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
