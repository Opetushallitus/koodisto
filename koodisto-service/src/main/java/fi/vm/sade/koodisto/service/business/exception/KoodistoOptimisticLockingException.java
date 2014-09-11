package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistoOptimisticLockingException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoOptimisticLockingException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.locking";

    public KoodistoOptimisticLockingException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoOptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoOptimisticLockingException(String message) {
        super(message);
    }

    public KoodistoOptimisticLockingException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
