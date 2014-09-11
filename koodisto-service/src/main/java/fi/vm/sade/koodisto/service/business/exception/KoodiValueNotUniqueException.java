package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodiValueNotUniqueException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiValueNotUniqueException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.value.not.unique";

    public KoodiValueNotUniqueException() {
        super(ERROR_MESSAGE);
    }

    public KoodiValueNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiValueNotUniqueException(String message) {
        super(message);
    }

    public KoodiValueNotUniqueException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
