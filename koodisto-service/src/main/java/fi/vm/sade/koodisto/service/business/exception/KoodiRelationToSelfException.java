package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodi value is empty or null
 *
 * @author wuoti
 *
 */
public class KoodiRelationToSelfException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiRelationToSelfException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.relation.to.self";

    public KoodiRelationToSelfException() {
        super(ERROR_MESSAGE);
    }

    public KoodiRelationToSelfException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodiRelationToSelfException(String message) {
        super(message);
    }

    public KoodiRelationToSelfException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
