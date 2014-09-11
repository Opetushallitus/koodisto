package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto nimi is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoNimiEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoNimiEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.name.empty";

    public KoodistoNimiEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoNimiEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNimiEmptyException(String message) {
        super(message);
    }

    public KoodistoNimiEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
