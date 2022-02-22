package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

public class KoodistonSuhdeContainsKoodinSuhdeException extends
SadeBusinessException {

    private static final long serialVersionUID = 4713895721067413817L;
    public static final String ERROR_KEY = KoodistonSuhdeContainsKoodinSuhdeException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.contains.codeelement.relations";

    public KoodistonSuhdeContainsKoodinSuhdeException(String message) {
        super(message);
    }
    
    public KoodistonSuhdeContainsKoodinSuhdeException(){
        super(ERROR_MESSAGE);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
