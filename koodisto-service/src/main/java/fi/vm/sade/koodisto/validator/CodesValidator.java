package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;

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
        ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codes"));
        ValidatorUtil.checkForBlank(validatable.getCodesGroupUri(), new KoodistoValidationException("error.validation.codesgroup"));
        ValidatorUtil.checkForBlank(validatable.getOrganisaatioOid(), new KoodistoValidationException("error.validation.organization"));
        ValidatorUtil.checkForNull(validatable.getVoimassaAlkuPvm(), new KoodistoValidationException("error.validation.startdate"));
        ValidatorUtil.checkBeginDateBeforeEndDate(validatable.getVoimassaAlkuPvm(), validatable.getVoimassaLoppuPvm(), new KoodistoValidationException("error.validation.enddate"));
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodistoDto validatable) {
        ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codes"));
        ValidatorUtil.checkForBlank(validatable.getKoodistoUri(), new KoodistoValidationException("error.validation.codesuri"));
        checkMetadatas(validatable.getMetadata());
    }

    private void checkRequiredMetadataFields(Collection<KoodistoMetadata> metadatas) {
        for (KoodistoMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.language"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoValidationException("error.validation.name"));
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new KoodistoValidationException("error.validation.metadata"));
        checkRequiredMetadataFields(metadatas);
    }

}
