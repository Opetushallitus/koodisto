package fi.vm.sade.koodisto.service.business.exception;

public class KoodistoValidationException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodiNotFoundException.class.getCanonicalName();

    public KoodistoValidationException() {
        super();
    }

    public KoodistoValidationException(String string) {
        super(string);
    }

    public KoodistoValidationException(Exception e) {
        super(e);
    }

    public KoodistoValidationException(String string, Exception e) {
        super(string, e);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
