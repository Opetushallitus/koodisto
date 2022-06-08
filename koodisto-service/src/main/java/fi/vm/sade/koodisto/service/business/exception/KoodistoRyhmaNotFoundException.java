package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

/**
 * Exception class for situations where koodistoryhma could not be found
 * 
 * @author wuoti
 * 
 */
public class KoodistoRyhmaNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaNotFoundException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.koodistoryhma.not.found";

    public KoodistoRyhmaNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoRyhmaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRyhmaNotFoundException(String message) {
        super(message);
    }

    public KoodistoRyhmaNotFoundException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
