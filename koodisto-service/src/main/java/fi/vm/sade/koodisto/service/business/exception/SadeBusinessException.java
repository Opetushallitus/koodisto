package fi.vm.sade.koodisto.service.business.exception;

public abstract class SadeBusinessException extends RuntimeException {
    private static final long serialVersionUID = -3166133180867859097L;

    public SadeBusinessException() {
    }

    public SadeBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SadeBusinessException(String message) {
        super(message);
    }

    public SadeBusinessException(Throwable cause) {
        super(cause);
    }

    public abstract String getErrorKey();
}