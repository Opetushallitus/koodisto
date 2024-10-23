package fi.vm.sade.koodisto.model.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KoodistoVersioKoodiVersioValidator.class)
@Documented
public @interface KoodistoVersioKoodiVersioConstraint {

    String message() default "{fi.vm.sade.koodisto.model.constraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
