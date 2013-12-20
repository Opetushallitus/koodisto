package fi.vm.sade.koodisto.model.constraint.fieldassert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldAssertValidator implements ConstraintValidator<FieldAssert, Object> {

    private String fieldName1;
    private String fieldName2;

    @SuppressWarnings("rawtypes")
    private Asserter asserterInstance;

    @Override
    public void initialize(FieldAssert constraintAnnotation) {
        try {
            fieldName1 = constraintAnnotation.field1();
            fieldName2 = constraintAnnotation.field2();

            Class<?> asserterClass = constraintAnnotation.asserter();
            if (!Asserter.class.isAssignableFrom(asserterClass)) {
                throw new RuntimeException("Asserter must be an instance of " + Asserter.class.getCanonicalName());
            }

            Constructor<?> declaredConstructor = constraintAnnotation.asserter().getDeclaredConstructor(
                    new Class<?>[] {});

            asserterInstance = (Asserter<?>) declaredConstructor.newInstance();
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Method getter1 = value.getClass().getMethod(
                    "get" + Character.toUpperCase(fieldName1.charAt(0)) + fieldName1.substring(1), new Class<?>[] {});

            Method getter2 = value.getClass().getMethod(
                    "get" + Character.toUpperCase(fieldName2.charAt(0)) + fieldName2.substring(1), new Class<?>[] {});

            if (!getter1.getReturnType().isAssignableFrom(getter2.getReturnType())) {
                throw new RuntimeException("Field types must be compatible with each other");
            }

            Object value1 = getter1.invoke(value, new Object[] {});
            Object value2 = getter2.invoke(value, new Object[] {});

            final boolean isValid = asserterInstance.assertTrue(value1, value2);

            return isValid;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
