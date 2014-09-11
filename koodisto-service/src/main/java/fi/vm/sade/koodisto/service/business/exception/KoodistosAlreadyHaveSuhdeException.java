package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistosAlreadyHaveSuhdeException extends SadeBusinessException {

    private static final long serialVersionUID = 155764709543824538L;
    public static final String ERROR_KEY = KoodistosAlreadyHaveSuhdeException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.already.have.relation";

    public KoodistosAlreadyHaveSuhdeException(String message) {
        super(message);
    }
    
    public KoodistosAlreadyHaveSuhdeException(){
        super(ERROR_MESSAGE);
    }

    public KoodistosAlreadyHaveSuhdeException(Throwable cause){
        super(ERROR_MESSAGE, cause);
    }
    
    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
