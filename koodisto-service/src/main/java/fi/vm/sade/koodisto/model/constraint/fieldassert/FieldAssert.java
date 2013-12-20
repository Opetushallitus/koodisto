package fi.vm.sade.koodisto.model.constraint.fieldassert;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldAssertValidator.class)
@Documented
public @interface FieldAssert {

    String message() default "{fi.vm.sade.koodisto.model.constraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field1();

    String field2();

    Class<?> asserter();

}
