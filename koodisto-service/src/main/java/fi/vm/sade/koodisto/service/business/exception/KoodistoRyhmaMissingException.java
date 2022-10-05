package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

public class KoodistoRyhmaMissingException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoRyhmaMissingException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.koodistoryhma.missing";
    public KoodistoRyhmaMissingException() {
        super(ERROR_MESSAGE);
    }
    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
