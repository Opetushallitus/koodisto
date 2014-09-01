package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;

public class ExtendedCodeElementValidator implements RestValidator<ExtendedKoodiDto> {

    private static final KoodistoValidationException TO_THROW = new KoodistoValidationException("error.validation.codeelement");

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void validate(ExtendedKoodiDto validatable, ValidationType type) {
        if (type == ValidationType.INSERT) {
            validateInsert(validatable);
        } else {
            validateUpdate(validatable);
        }
    }

    @Override
    public void validateInsert(ExtendedKoodiDto validatable) {
        try {
            ValidatorUtil.checkForNull(validatable, TO_THROW);

            ValidatorUtil.checkForNull(validatable.getIncludesCodeElements(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getLevelsWithCodeElements(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getWithinCodeElements(), TO_THROW);

            ValidatorUtil.checkForBlank(validatable.getKoodiUri(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getVoimassaAlkuPvm(), TO_THROW);
            ValidatorUtil.checkForBlank(validatable.getKoodiArvo(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getVersio(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getVersion(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getTila(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getMetadata(), TO_THROW);
            ValidatorUtil.checkForNull(validatable.getKoodisto(), TO_THROW);

            checkMetadatas(validatable.getMetadata());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    @Override
    public void validateUpdate(ExtendedKoodiDto validatable) {
        validateInsert(validatable);
    }

    private void checkRequiredMetadataFields(Collection<KoodiMetadata> metadatas) {
        for (KoodiMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.metadata"));
            logger.warn("No koodi nimi defined for language " + md.getKieli().name());
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodiNimiEmptyException());
        }
    }

    private void checkMetadatas(Collection<KoodiMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException());
        checkRequiredMetadataFields(metadatas);
    }

}
