package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistosHaveDifferentOrganizationsException extends SadeBusinessException {

    private static final long serialVersionUID = 155764709543824538L;
    public static final String ERROR_KEY = KoodistosAlreadyHaveSuhdeException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.have.different.organizations";

    public KoodistosHaveDifferentOrganizationsException(String message) {
        super(message);
    }
    
    public KoodistosHaveDifferentOrganizationsException(){
        super(ERROR_MESSAGE);
    }
    
    public KoodistosHaveDifferentOrganizationsException(Throwable cause){
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
