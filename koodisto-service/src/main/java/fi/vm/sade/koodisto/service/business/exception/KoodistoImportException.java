package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 12.04
 */
public class KoodistoImportException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoImportException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.importing";

    public KoodistoImportException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoImportException(String message) {
        super(message);
    }

    public KoodistoImportException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
