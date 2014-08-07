package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class ValidatorUtil {
    
    public static void checkForBlank(String toCheck, String error) {
        if (StringUtils.isBlank(toCheck)) {
            throw new IllegalArgumentException(error);
        }
    }
    
    public static void checkForNull(Object toCheck, String error) {
        if (toCheck == null) {
            throw new IllegalArgumentException(error);
        }
    }
    
    public static void checkForNull(Object toCheck, RuntimeException toThrow) {
        if (toCheck == null) {
            throw toThrow;
        }
    }
    
    public static void checkForBlank(String toCheck, RuntimeException toThrow) {
        if (StringUtils.isBlank(toCheck)) {
            throw toThrow;
        }
    }
    
    public static void checkCollectionIsNotNullOrEmpty(Collection<?> toCheck, RuntimeException toThrow) {
        if (toCheck == null || toCheck.isEmpty()) {
            throw toThrow;
        }
    }
    
    public static void checkForGreaterThan(Integer toCheck, int greaterThanThis, RuntimeException toThrow) {
        if (toCheck == null || !(toCheck > greaterThanThis)) {
            throw toThrow;
        }
    }

}
