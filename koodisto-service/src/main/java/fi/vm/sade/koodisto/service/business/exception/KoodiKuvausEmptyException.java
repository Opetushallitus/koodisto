package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi kuvaus is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodiKuvausEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiKuvausEmptyException.class.getCanonicalName();

    public KoodiKuvausEmptyException() {
        super();
    }

    public KoodiKuvausEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiKuvausEmptyException(String message) {
        super(message);
    }

    public KoodiKuvausEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
