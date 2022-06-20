package fi.vm.sade.koodisto.validator;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorUtil {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorUtil.class);

    private ValidatorUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkForBlank(String toCheck, RuntimeException toThrow) {
        if (Strings.isNullOrEmpty(toCheck) || toCheck.isBlank()) {
            logger.warn("Failure during isBlank check:{} ", toThrow.getMessage());
            throw toThrow;
        }
    }
}
