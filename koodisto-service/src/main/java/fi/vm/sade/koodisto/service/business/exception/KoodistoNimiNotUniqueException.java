package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where the nimi set for koodisto is not unique
 * 
 * @author wuoti
 * 
 */
public class KoodistoNimiNotUniqueException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoNimiNotUniqueException.class.getCanonicalName();

    public KoodistoNimiNotUniqueException() {
        super();
    }

    public KoodistoNimiNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNimiNotUniqueException(String message) {
        super(message);
    }

    public KoodistoNimiNotUniqueException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
