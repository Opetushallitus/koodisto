package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 13.32
 */
public class KoodistoExportException extends SadeBusinessException{
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoExportException.class.getCanonicalName();

    public KoodistoExportException() {
        super();
    }

    public KoodistoExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoExportException(String message) {
        super(message);
    }

    public KoodistoExportException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
