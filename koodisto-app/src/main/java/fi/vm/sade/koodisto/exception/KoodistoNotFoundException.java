package fi.vm.sade.koodisto.exception;

public class KoodistoNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -6127688043798584670L;

    public KoodistoNotFoundException() {
        super();
    }

    public KoodistoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoNotFoundException(String message) {
        super(message);
    }

    public KoodistoNotFoundException(Throwable cause) {
        super(cause);
    }

}
