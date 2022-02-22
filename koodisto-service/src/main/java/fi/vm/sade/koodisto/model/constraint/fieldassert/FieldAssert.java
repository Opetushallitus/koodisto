package fi.vm.sade.koodisto.model.constraint.fieldassert;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

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
