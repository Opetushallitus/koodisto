package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 12.41
 */
public class InvalidKoodiCsvLineException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoImportException.class.getCanonicalName();

    public InvalidKoodiCsvLineException() {
        super();
    }

    public InvalidKoodiCsvLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKoodiCsvLineException(String message) {
        super(message);
    }

    public InvalidKoodiCsvLineException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
