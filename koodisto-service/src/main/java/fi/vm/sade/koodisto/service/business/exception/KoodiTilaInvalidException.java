package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

public class KoodiTilaInvalidException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiTilaInvalidException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.status.invalid";

    public KoodiTilaInvalidException() {
        super(ERROR_MESSAGE);
    }

    public KoodiTilaInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiTilaInvalidException(String message) {
        super(message);
    }

    public KoodiTilaInvalidException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
