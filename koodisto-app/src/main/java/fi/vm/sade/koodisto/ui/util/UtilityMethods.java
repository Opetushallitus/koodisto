package fi.vm.sade.koodisto.ui.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

public abstract class UtilityMethods {
    public static <T> boolean allFieldsAreNullOrEmpty(Class<T> clazz, T instance, String... ignoredFields) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        boolean allFieldsAreNullOrEmpty = true;

        methods: for (Method m : clazz.getMethods()) {
            if (!m.getName().startsWith("get") || m.getName().equals("getClass") || m.getParameterTypes().length > 0) {
                continue;
            }

            // Skip ignored fields
            if (ignoredFields != null && ignoredFields.length > 0) {
                for (String f : ignoredFields) {
                    if (StringUtils.isBlank(f)) {
                        continue;
                    }

                    if (m.getName().equals("get" + f.substring(0, 1).toUpperCase() + f.substring(1))) {
                        continue methods;
                    }
                }
            }

            Object returnValue = m.invoke(instance, new Object[] {});

            if (returnValue != null && (String.class.isAssignableFrom(m.getReturnType()) && StringUtils.isNotBlank((String) returnValue))) {
                allFieldsAreNullOrEmpty = false;
                break;
            }
        }

        return allFieldsAreNullOrEmpty;
    }
}
