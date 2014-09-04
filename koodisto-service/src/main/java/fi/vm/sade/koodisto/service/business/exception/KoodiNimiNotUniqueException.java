package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where the nimi set for koodi is not unique
 * 
 * @author wuoti
 * 
 */
public class KoodiNimiNotUniqueException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNimiNotUniqueException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.name.not.unique";

    public KoodiNimiNotUniqueException() {
        super(ERROR_MESSAGE);
    }

    public KoodiNimiNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNimiNotUniqueException(String message) {
        super(message);
    }

    public KoodiNimiNotUniqueException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
