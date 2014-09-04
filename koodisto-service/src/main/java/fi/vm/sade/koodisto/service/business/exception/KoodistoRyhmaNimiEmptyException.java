package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto nimi is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoRyhmaNimiEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaNimiEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codesgroup.name.empty";

    public KoodistoRyhmaNimiEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoRyhmaNimiEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRyhmaNimiEmptyException(String message) {
        super(message);
    }

    public KoodistoRyhmaNimiEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
