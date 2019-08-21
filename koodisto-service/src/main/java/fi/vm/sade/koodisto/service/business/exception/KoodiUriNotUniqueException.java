package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

/**
 * Exception class for situations where koodisto URI is not unique
 * 
 * @author wuoti
 * 
 */
public class KoodiUriNotUniqueException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiUriNotUniqueException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.uri.not.unique";

    public KoodiUriNotUniqueException() {
        super(ERROR_MESSAGE);
    }

    public KoodiUriNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiUriNotUniqueException(String message) {
        super(message);
    }

    public KoodiUriNotUniqueException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
