package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where koodi value is empty or null
 * 
 * @author wuoti
 * 
 */
public class KoodistoRelationToSelfException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRelationToSelfException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.relation.to.self";

    public KoodistoRelationToSelfException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoRelationToSelfException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoRelationToSelfException(String message) {
        super(message);
    }

    public KoodistoRelationToSelfException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
