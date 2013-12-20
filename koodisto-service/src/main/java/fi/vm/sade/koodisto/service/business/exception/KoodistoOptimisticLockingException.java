package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistoOptimisticLockingException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoOptimisticLockingException.class.getCanonicalName();

    public KoodistoOptimisticLockingException() {
        super();
    }

    public KoodistoOptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoOptimisticLockingException(String message) {
        super(message);
    }

    public KoodistoOptimisticLockingException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
