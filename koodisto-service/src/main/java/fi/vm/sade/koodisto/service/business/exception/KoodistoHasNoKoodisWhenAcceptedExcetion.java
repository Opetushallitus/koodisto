package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistoHasNoKoodisWhenAcceptedExcetion extends SadeBusinessException {

    public static final String ERROR_KEY = KoodistoNimiNotUniqueException.class.getCanonicalName();

    public KoodistoHasNoKoodisWhenAcceptedExcetion(String string) {
        super(string);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}
