package fi.vm.sade.koodisto.service.business.exception;

public class KoodisHaveDifferentOrganizationsException extends SadeBusinessException {

    private static final long serialVersionUID = 155764709543824538L;
    public static final String ERROR_KEY = KoodistosAlreadyHaveSuhdeException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codeelement.not.in.same.organization";

    public KoodisHaveDifferentOrganizationsException(String message) {
        super(message);
    }

    public KoodisHaveDifferentOrganizationsException() {
        super(ERROR_MESSAGE);
    }

    public KoodisHaveDifferentOrganizationsException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
