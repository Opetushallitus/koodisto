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

    public KoodiNimiNotUniqueException() {
        super();
    }

    public KoodiNimiNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNimiNotUniqueException(String message) {
        super(message);
    }

    public KoodiNimiNotUniqueException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
