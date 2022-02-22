package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

public class KoodiVersioNotPassiivinenException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiVersioNotPassiivinenException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.not.passive";

    public KoodiVersioNotPassiivinenException() {
        super(ERROR_MESSAGE);
    }

    public KoodiVersioNotPassiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiVersioNotPassiivinenException(String message) {
        super(message);
    }

    public KoodiVersioNotPassiivinenException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
