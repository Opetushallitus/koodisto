package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto nimi is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoRyhmaNotEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaNotEmptyException.class.getCanonicalName();

    public KoodistoRyhmaNotEmptyException() {
        super();
    }

    public KoodistoRyhmaNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRyhmaNotEmptyException(String message) {
        super(message);
    }

    public KoodistoRyhmaNotEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
