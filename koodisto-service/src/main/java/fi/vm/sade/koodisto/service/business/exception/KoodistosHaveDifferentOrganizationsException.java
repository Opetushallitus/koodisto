package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistosHaveDifferentOrganizationsException extends SadeBusinessException {

    private static final long serialVersionUID = 155764709543824538L;

    public static final String ERROR_KEY = KoodistosAlreadyHaveSuhdeException.class.getCanonicalName();

    public KoodistosHaveDifferentOrganizationsException(String message) {
        super(message);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
