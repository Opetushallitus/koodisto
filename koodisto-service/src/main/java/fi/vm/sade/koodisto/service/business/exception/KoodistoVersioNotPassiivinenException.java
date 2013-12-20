package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistoVersioNotPassiivinenException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoVersioNotPassiivinenException.class.getCanonicalName();

    public KoodistoVersioNotPassiivinenException() {
        super();
    }

    public KoodistoVersioNotPassiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoVersioNotPassiivinenException(String message) {
        super(message);
    }

    public KoodistoVersioNotPassiivinenException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
