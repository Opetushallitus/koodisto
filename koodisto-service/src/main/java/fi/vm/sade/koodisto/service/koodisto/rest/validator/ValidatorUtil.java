package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import org.apache.commons.lang.StringUtils;

public class ValidatorUtil {
    
    public static void checkForNullOrEmpty(String toCheck, String error) {
        if (StringUtils.isBlank(toCheck)) {
            throw new IllegalArgumentException(error);
        }
    }
    
    public static void checkForNull(Object toCheck, String error) {
        if (toCheck == null) {
            throw new IllegalArgumentException(error);
        }
    }

}
