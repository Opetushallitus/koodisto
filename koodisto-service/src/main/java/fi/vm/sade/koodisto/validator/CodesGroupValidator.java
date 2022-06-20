package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;

import java.util.Collection;

public class CodesGroupValidator implements RestValidator<KoodistoRyhmaDto> {

    @Override
    public void validate(KoodistoRyhmaDto validatable, ValidationType type) {
        if (type == ValidationType.INSERT) {
            validateInsert(validatable);
        } else {
            validateUpdate(validatable);
        }
    }

    @Override
    public void validateInsert(KoodistoRyhmaDto validatable) {
    }

    @Override
    public void validateUpdate(KoodistoRyhmaDto validatable) {
        try {
            ValidatorUtil.checkForBlank(validatable.getKoodistoRyhmaUri(), new KoodistoRyhmaUriEmptyException());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }
}
