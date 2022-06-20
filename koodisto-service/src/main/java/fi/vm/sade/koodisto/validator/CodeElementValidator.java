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
        ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeelement"));
        ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), new KoodistoValidationException("error.validation.value"));
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodiDto validatable) {
        ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeelement"));

        ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodistoValidationException("error.validation.codeelementuri"));
        ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), new KoodistoValidationException("error.validation.value"));
        ValidatorUtil.checkForNull(validatable.getVersio(), new KoodistoValidationException("error.validation.versio"));
        ValidatorUtil.checkForNull(validatable.getTila(), new KoodistoValidationException("error.validation.status"));

        checkMetadatas(validatable.getMetadata());
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadataDto> metadatas) {
        for (KoodiMetadataDto md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.language"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoValidationException("error.validation.name"));
        }
    }

    private void checkMetadatas(Collection<KoodiMetadataDto> metadatas) {
        checkRequiredMetadataFields(metadatas);
    }

}
