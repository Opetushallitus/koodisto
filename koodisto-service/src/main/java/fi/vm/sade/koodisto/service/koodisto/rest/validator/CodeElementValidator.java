package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;

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
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeElement"));
            checkMetadatas(validatable.getMetadata());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    @Override
    public void validateUpdate(KoodiDto validatable) {
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codeElement"));
            ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodiUriEmptyException());
            checkMetadatas(validatable.getMetadata());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
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
