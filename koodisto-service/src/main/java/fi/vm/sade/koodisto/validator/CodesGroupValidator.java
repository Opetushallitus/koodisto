package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.validator.Validatable.ValidationType;

import java.util.Collection;

public class CodesGroupValidator implements RestValidator<KoodistoRyhmaDto> {

    @Override
    public void validate(KoodistoRyhmaDto validatable, ValidationType type) {
        if (type == ValidationType.INSERT) {
            validateInsert(validatable);
        } else {
            validateUpdate(validatable);
        }
    }

    @Override
    public void validateInsert(KoodistoRyhmaDto validatable) {
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codesgroup"));
            checkMetadatas(validatable.getKoodistoRyhmaMetadatas());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    @Override
    public void validateUpdate(KoodistoRyhmaDto validatable) {
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codesgroup"));
            ValidatorUtil.checkForBlank(validatable.getKoodistoRyhmaUri(), new KoodistoRyhmaUriEmptyException());
            checkMetadatas(validatable.getKoodistoRyhmaMetadatas());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    private void checkRequiredMetadataFields(Collection<KoodistoRyhmaMetadata> metadatas) {
        boolean atLeastOneFieldIsValid = false;
        for (KoodistoRyhmaMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.metadata"));
            if(!md.getNimi().isBlank()){
                atLeastOneFieldIsValid = true;
            }
        }
        if(!atLeastOneFieldIsValid){
            throw new KoodistoValidationException("error.validation.metadata");
        }
    }

    private void checkMetadatas(Collection<KoodistoRyhmaMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException());
        checkRequiredMetadataFields(metadatas);
    }

}
