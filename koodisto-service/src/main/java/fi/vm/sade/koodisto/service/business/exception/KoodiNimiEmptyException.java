package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi nimi is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodiNimiEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNimiEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.name.empty";

    public KoodiNimiEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodiNimiEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNimiEmptyException(String message) {
        super(message);
    }

    public KoodiNimiEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
