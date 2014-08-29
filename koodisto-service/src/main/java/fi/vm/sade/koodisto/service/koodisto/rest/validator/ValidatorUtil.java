package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorUtil {
    private static Logger logger = LoggerFactory.getLogger(ValidatorUtil.class);

    public static void checkForNull(Object toCheck, RuntimeException toThrow) {
        if (toCheck == null) {
            logger.warn("Failure during null check: " + toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkForBlank(String toCheck, RuntimeException toThrow) {
        if (StringUtils.isBlank(toCheck)) {
            logger.warn("Failure during isBlank check: " + toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkCollectionIsNotNullOrEmpty(Collection<?> toCheck, RuntimeException toThrow) {
        if (toCheck == null || toCheck.isEmpty()) {
            logger.warn("Failure during null and empty check: " + toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkForGreaterThan(Integer toCheck, int greaterThanThis, RuntimeException toThrow) {
        if (toCheck == null || !(toCheck > greaterThanThis)) {
            logger.warn("Failure during greaterThan check: " + toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void validateArgs(String[] keys, Object... values) {
        int i = 0;
        for (Object object : values) {
            KoodistoValidationException toThrow = new KoodistoValidationException("error.validation."+keys[i]);
            checkForNull(object, toThrow);
            if (object.getClass().equals(String.class)) {
                checkForBlank((String) object, toThrow);
            }
            i++;
        }
    }

}
