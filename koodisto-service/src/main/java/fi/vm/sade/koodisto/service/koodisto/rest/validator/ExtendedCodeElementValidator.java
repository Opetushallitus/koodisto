package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ExtendedCodeElementValidator implements RestValidator<ExtendedKoodiDto> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void validate(ExtendedKoodiDto validatable, ValidationType type) {
            validateUpdate(validatable);
    }

    @Override
    public void validateInsert(ExtendedKoodiDto validatable) {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeelement"));

            ValidatorUtil.checkForNull(validatable.getIncludesCodeElements(), new KoodistoValidationException("error.validation.codeelement.relations"));
            ValidatorUtil.checkForNull(validatable.getLevelsWithCodeElements(), new KoodistoValidationException("error.validation.codeelement.relations"));
            ValidatorUtil.checkForNull(validatable.getWithinCodeElements(), new KoodistoValidationException("error.validation.codeelement.relations"));

            ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodistoValidationException("error.validation.codeelementuri"));
            ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), new KoodistoValidationException("error.validation.value"));
            ValidatorUtil.checkForNull(validatable.getVersio(), new KoodistoValidationException("error.validation.versio"));
            ValidatorUtil.checkForNull(validatable.getTila(), new KoodistoValidationException("error.validation.status"));
            ValidatorUtil.checkForNull(validatable.getMetadata(), new KoodistoValidationException("error.validation.metadata"));

            checkMetadatas(validatable.getMetadata());

            ValidatorUtil.checkForNull(validatable.getVoimassaAlkuPvm(), new KoodistoValidationException("error.validation.startdate"));
            ValidatorUtil.checkBeginDateBeforeEndDate(validatable.getVoimassaAlkuPvm(), validatable.getVoimassaLoppuPvm(),  new KoodistoValidationException("error.validation.enddate"));
    }

    @Override
    public void validateUpdate(ExtendedKoodiDto validatable) {
        validateInsert(validatable);
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadata> metadatas) {
        for (KoodiMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.kieli"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoValidationException("error.validation.nimi"));
        }
    }

    private void checkMetadatas(Collection<KoodiMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException());
        checkRequiredMetadataFields(metadatas);
    }

}
