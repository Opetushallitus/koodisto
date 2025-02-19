package fi.vm.sade.koodisto.service.business.exception;

public class KoodistoRyhmaExistsException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaExistsException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.koodistoryhma.already.exists";
    public KoodistoRyhmaExistsException() {
        super(ERROR_MESSAGE);
    }
    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
