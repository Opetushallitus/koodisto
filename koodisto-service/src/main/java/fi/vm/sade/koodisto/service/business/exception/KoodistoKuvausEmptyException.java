package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodisto kuvaus is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoKuvausEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoKuvausEmptyException.class.getCanonicalName();

    public KoodistoKuvausEmptyException() {
        super();
    }

    public KoodistoKuvausEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoKuvausEmptyException(String message) {
        super(message);
    }

    public KoodistoKuvausEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
