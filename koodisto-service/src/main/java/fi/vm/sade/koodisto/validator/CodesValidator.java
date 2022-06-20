package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;

import java.util.Collection;

public class CodesValidator implements RestValidator<KoodistoDto> {

    @Override
    public void validate(KoodistoDto validatable, ValidationType type) {
        if (type == ValidationType.INSERT) {
            validateInsert(validatable);
        } else {
            validateUpdate(validatable);
        }
    }

    @Override
    public void validateInsert(KoodistoDto validatable) {
    }

    @Override
    public void validateUpdate(KoodistoDto validatable) {
    }
}
