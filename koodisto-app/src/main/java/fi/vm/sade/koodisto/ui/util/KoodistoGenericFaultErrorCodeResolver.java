package fi.vm.sade.koodisto.ui.util;

import org.springframework.context.NoSuchMessageException;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.GenericFault;

public class KoodistoGenericFaultErrorCodeResolver {

    private KoodistoGenericFaultErrorCodeResolver(){
        // Never called
    }
    
    public static String getErrorMessageForGenericFault(GenericFault genericFault) {
        return getErrorMessageForCode(genericFault.getFaultInfo().getErrorCode());
    }

    public static String getErrorMessageForCode(final String errorCode) {
        String message = "";
        String messageKey = "serviceFault." + errorCode;

        try {
            message = I18N.getMessage("serviceFault." + errorCode);
            if (message.equals(messageKey)) {
                message = I18N.getMessage("serviceFault.unknown", errorCode);
            }
        } catch (NoSuchMessageException e) {
            message = I18N.getMessage("serviceFault.unknown", errorCode);
        }

        return message;
    }
}
