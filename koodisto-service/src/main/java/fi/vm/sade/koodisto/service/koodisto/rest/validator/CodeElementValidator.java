package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class CodeElementValidator implements RestValidator<KoodiDto> {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        ValidatorUtil.checkForNull(validatable.getVoimassaAlkuPvm(), new KoodistoValidationException("error.validation.startdate"));
        ValidatorUtil.checkBeginDateBeforeEndDate(validatable.getVoimassaAlkuPvm(), validatable.getVoimassaLoppuPvm(), new KoodistoValidationException("error.validation.enddate"));
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodiDto validatable) {
        ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeelement"));

        ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodistoValidationException("error.validation.codeelementuri"));
        ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), new KoodistoValidationException("error.validation.value"));
        ValidatorUtil.checkForNull(validatable.getVersio(), new KoodistoValidationException("error.validation.versio"));
        ValidatorUtil.checkForNull(validatable.getTila(), new KoodistoValidationException("error.validation.status"));
        ValidatorUtil.checkBeginDateBeforeEndDate(validatable.getVoimassaAlkuPvm(), validatable.getVoimassaLoppuPvm(), new KoodistoValidationException("error.validation.enddate"));

        checkMetadatas(validatable.getMetadata());
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadata> metadatas) {
        for (KoodiMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.language"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoValidationException("error.validation.name"));
        }
    }

    private void checkMetadatas(Collection<KoodiMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new KoodistoValidationException("error.validation.metadata"));
        checkRequiredMetadataFields(metadatas);
    }

}
