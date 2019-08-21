package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 12.41
 */
public class InvalidKoodiCsvLineException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoImportException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.invalid.csv.file";
    
    
    public InvalidKoodiCsvLineException() {
        super(ERROR_MESSAGE);
    }

    public InvalidKoodiCsvLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKoodiCsvLineException(String message) {
        super(message);
    }

    public InvalidKoodiCsvLineException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
