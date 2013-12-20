package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodistoryhma could not be found
 * 
 * @author wuoti
 * 
 */
public class KoodistoRyhmaNotFoundException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaNotFoundException.class.getCanonicalName();

    public KoodistoRyhmaNotFoundException() {
        super();
    }

    public KoodistoRyhmaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRyhmaNotFoundException(String message) {
        super(message);
    }

    public KoodistoRyhmaNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
