package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where metadata is empty
 * 
 * @author wuoti
 * 
 */
public class MetadataEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = MetadataEmptyException.class.getCanonicalName();

    public MetadataEmptyException() {
        super();
    }

    public MetadataEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataEmptyException(String message) {
        super(message);
    }

    public MetadataEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
