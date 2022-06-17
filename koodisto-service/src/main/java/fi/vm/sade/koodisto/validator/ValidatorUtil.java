package fi.vm.sade.koodisto.validator;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;

public class ValidatorUtil {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorUtil.class);

    private ValidatorUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkForNull(Object toCheck, RuntimeException toThrow) {
        if (toCheck == null) {
            logger.warn("Failure during null check:{}", toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkForBlank(String toCheck, RuntimeException toThrow) {
        if (Strings.isNullOrEmpty(toCheck) || toCheck.isBlank()) {
            logger.warn("Failure during isBlank check:{} ", toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkCollectionIsNotNullOrEmpty(Collection<?> toCheck, RuntimeException toThrow) {
        if (toCheck == null || toCheck.isEmpty()) {
            logger.warn("Failure during null and empty check: {}", toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void checkBeginDateBeforeEndDate(Date start, Date end, RuntimeException toThrow) {
        if (start != null && end != null && end.before(start)) {
            logger.warn("Failure during date validation: {}", toThrow.getMessage());
            throw toThrow;
        }
    }

    public static void validateDateParameters(Integer dayOfMonth, Integer month, Integer year, Integer hourOfDay, Integer minute, Integer second) {
        if (dayOfMonth < 1 || dayOfMonth > 31 || month < 1 || month > 12 || year < 1 || hourOfDay < 0 || hourOfDay > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
            throw new KoodistoValidationException("Parameters provided for date were invalid.");
        }

    }

}
