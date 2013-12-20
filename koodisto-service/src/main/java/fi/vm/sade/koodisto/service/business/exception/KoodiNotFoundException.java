package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where the koodi could not be found
 * 
 * @author wuoti
 * 
 */
public class KoodiNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNotFoundException.class.getCanonicalName();

    public KoodiNotFoundException() {
        super();
    }

    public KoodiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiNotFoundException(String message) {
        super(message);
    }

    public KoodiNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
