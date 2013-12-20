package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

/**
 * Exception class for situations where search criteria is empty
 * 
 * @author wuoti
 * 
 */
public class SearchCriteriaEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = SearchCriteriaEmptyException.class.getCanonicalName();

    public SearchCriteriaEmptyException() {
        super();
    }

    public SearchCriteriaEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchCriteriaEmptyException(String message) {
        super(message);
    }

    public SearchCriteriaEmptyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
