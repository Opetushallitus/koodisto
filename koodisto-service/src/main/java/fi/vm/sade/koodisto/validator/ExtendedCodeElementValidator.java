package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

import java.util.Collection;

public class ExtendedCodeElementValidator implements RestValidator<ExtendedKoodiDto> {

    private static final String KOODI_RELATIONS_ERROR_CODE = "error.validation.codeelement.relations";

    @Override
    public void validate(ExtendedKoodiDto validatable, ValidationType type) {
            validateUpdate(validatable);
    }

    @Override
    public void validateInsert(ExtendedKoodiDto validatable) {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeelement"));

            ValidatorUtil.checkForNull(validatable.getIncludesCodeElements(), new KoodistoValidationException(KOODI_RELATIONS_ERROR_CODE));
            ValidatorUtil.checkForNull(validatable.getLevelsWithCodeElements(), new KoodistoValidationException(KOODI_RELATIONS_ERROR_CODE));
            ValidatorUtil.checkForNull(validatable.getWithinCodeElements(), new KoodistoValidationException(KOODI_RELATIONS_ERROR_CODE));

            ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodistoValidationException("error.validation.codeelementuri"));
            ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), new KoodistoValidationException("error.validation.value"));
            ValidatorUtil.checkForNull(validatable.getVersio(), new KoodistoValidationException("error.validation.versio"));
            ValidatorUtil.checkForNull(validatable.getTila(), new KoodistoValidationException("error.validation.status"));
            ValidatorUtil.checkForNull(validatable.getMetadata(), new KoodistoValidationException("error.validation.metadata"));

            checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(ExtendedKoodiDto validatable) {
        validateInsert(validatable);
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadataDto> metadatas) {
        for (KoodiMetadataDto md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.kieli"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoValidationException("error.validation.nimi"));
        }
    }

    private void checkMetadatas(Collection<KoodiMetadataDto> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException());
        checkRequiredMetadataFields(metadatas);
    }

}
