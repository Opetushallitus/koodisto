package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

/**
 * Exception class for situations where search criteria is empty
 * 
 * @author wuoti
 * 
 */
public class SearchCriteriaEmptyException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = SearchCriteriaEmptyException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.search.criteria.empty";

    public SearchCriteriaEmptyException() {
        super(ERROR_MESSAGE);
    }

    public SearchCriteriaEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchCriteriaEmptyException(String message) {
        super(message);
    }

    public SearchCriteriaEmptyException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
