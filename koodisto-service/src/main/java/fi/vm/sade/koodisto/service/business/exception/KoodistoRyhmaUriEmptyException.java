package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodistoryhma URI is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoRyhmaUriEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaUriEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codesgroup.uri.empty";

    public KoodistoRyhmaUriEmptyException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoRyhmaUriEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRyhmaUriEmptyException(String message) {
        super(message);
    }

    public KoodistoRyhmaUriEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
