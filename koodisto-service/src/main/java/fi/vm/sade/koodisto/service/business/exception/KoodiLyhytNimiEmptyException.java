package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi lyhyt nimi is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodiLyhytNimiEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiLyhytNimiEmptyException.class.getCanonicalName();

    public KoodiLyhytNimiEmptyException() {
        super();
    }

    public KoodiLyhytNimiEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiLyhytNimiEmptyException(String message) {
        super(message);
    }

    public KoodiLyhytNimiEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
