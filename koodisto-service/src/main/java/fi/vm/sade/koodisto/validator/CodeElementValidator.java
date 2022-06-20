package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;

import java.util.Collection;

public class CodeElementValidator implements RestValidator<KoodiDto> {

    @Override
    public void validate(KoodiDto validatable, ValidationType type) {
        if (type == ValidationType.INSERT) {
            validateInsert(validatable);
        } else {
            validateUpdate(validatable);
        }
    }

    @Override
    public void validateInsert(KoodiDto validatable) {
    }

    @Override
    public void validateUpdate(KoodiDto validatable) {
        ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodistoValidationException("error.validation.codeelementuri"));
    }
}
