package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import com.google.common.base.Strings;

public class ValidatorUtil {
    
    public static void checkForNullOrEmpty(String toCheck, String error) {
        if (Strings.isNullOrEmpty(toCheck)) {
            throw new IllegalArgumentException(error);
        }
    }
    
    public static void checkForNull(Object toCheck, String error) {
        if (toCheck == null) {
            throw new IllegalArgumentException(error);
        }
    }

}
