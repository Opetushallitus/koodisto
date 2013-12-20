package fi.vm.sade.koodisto.model.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;

public class KoodistoVersioKoodiVersioValidator implements
        ConstraintValidator<KoodistoVersioKoodiVersioConstraint, KoodistoVersioKoodiVersio> {

    @Override
    public void initialize(KoodistoVersioKoodiVersioConstraint constraintAnnotation) {
        // No operation
    }

    @Override
    public boolean isValid(KoodistoVersioKoodiVersio value, ConstraintValidatorContext context) {
        if (value.getKoodistoVersio() == null || value.getKoodiVersio() == null
                || value.getKoodistoVersio().getKoodisto() == null || value.getKoodiVersio().getKoodi() == null) {
            throw new RuntimeException("Both koodisto versio and koodi versio must not be null");
        }

        final boolean isValid = value.getKoodiVersio().getKoodi().getKoodisto()
                .equals(value.getKoodistoVersio().getKoodisto());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "{fi.vm.sade.koodisto.model.constraint.KoodistoVersioKoodiVersioConstraint.message}")
                    .addConstraintViolation();
        }

        return isValid;
    }

}
