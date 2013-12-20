package fi.vm.sade.koodisto.ui.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public abstract class ValidatorUtil {

    public static boolean allStringsAreBlank(String... strings) {
        return !atLeastOneStringIsNotBlank(strings);
    }

    public static boolean atLeastOneStringIsNotBlank(String... strings) {
        boolean valid = false;
        for (String s : strings) {
            if (StringUtils.isNotBlank(s)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static boolean dateIsEitherNullOrAfterDate(Date date, Date after) {
        return after == null || after.after(date);
    }

    public static boolean allStringsAreBlankOrAllStringsAreNotBlank(String... strings) {
        Boolean previousIsBlank = null;

        boolean result = true;
        for (String s : strings) {
            boolean thisIsBlank = StringUtils.isBlank(s);

            if (previousIsBlank == null) {
                previousIsBlank = thisIsBlank;
            } else if (thisIsBlank != previousIsBlank.booleanValue()) {
                result = false;
                break;
            }
        }

        return result;
    }
}
