package fi.vm.sade.koodisto.model.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KoodistoVersioKoodiVersioValidator.class)
@Documented
public @interface KoodistoVersioKoodiVersioConstraint {

    String message() default "{fi.vm.sade.koodisto.model.constraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
