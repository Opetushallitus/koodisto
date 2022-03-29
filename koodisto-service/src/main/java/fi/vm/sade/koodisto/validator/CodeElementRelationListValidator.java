package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;

public class CodeElementRelationListValidator implements RestValidator<KoodiRelaatioListaDto> {

    private static final KoodistoValidationException TO_THROW = new KoodistoValidationException("error.validation.codeelementrelationlist");

    @Override
    public void validate(KoodiRelaatioListaDto validatable, ValidationType type) {
        validateInsert(validatable);
    }

    @Override
    public void validateInsert(KoodiRelaatioListaDto validatable) {
        ValidatorUtil.checkForNull(validatable, TO_THROW);
        ValidatorUtil.checkForBlank(validatable.getCodeElementUri(), TO_THROW);
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(validatable.getRelations(), TO_THROW);
        ValidatorUtil.checkForBlank(validatable.getRelationType(), TO_THROW);
    }

    @Override
    public void validateUpdate(KoodiRelaatioListaDto validatable) {
        validateInsert(validatable);
    }
}
