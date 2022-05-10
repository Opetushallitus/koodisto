package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 12.04
 */
public class KoodistoImportException extends SadeBusinessException {
    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoImportException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.importing";

    public KoodistoImportException(String message) {
        super(message);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }
}
