package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where metadata is empty
 *
 * @author wuoti
 *
 */
public class MetadataEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = MetadataEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.metadata.empty";

    public MetadataEmptyException() {
        super(ERROR_MESSAGE);
    }

    public MetadataEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataEmptyException(String message) {
        super(message);
    }

    public MetadataEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
