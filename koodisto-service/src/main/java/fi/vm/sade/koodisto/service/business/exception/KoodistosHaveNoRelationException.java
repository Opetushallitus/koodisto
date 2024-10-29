package fi.vm.sade.koodisto.service.business.exception;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 13.32
 */
public class KoodistosHaveNoRelationException extends SadeBusinessException{
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistosHaveNoRelationException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.codes.have.no.relation";

    public KoodistosHaveNoRelationException() {
        super(ERROR_MESSAGE);
    }

    public KoodistosHaveNoRelationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistosHaveNoRelationException(String message) {
        super(message);
    }

    public KoodistosHaveNoRelationException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
