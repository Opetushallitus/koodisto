package fi.vm.sade.koodisto.model.constraint;

import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.constraint.exception.KoodistoValidatorRuntimeException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
            throw new KoodistoValidatorRuntimeException("Both koodisto versio and koodi versio must not be null");
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
