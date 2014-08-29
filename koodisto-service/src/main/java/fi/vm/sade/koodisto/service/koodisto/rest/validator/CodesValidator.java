package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;

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
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codes"));
            ValidatorUtil.checkForBlank(validatable.getCodesGroupUri(), new KoodistoValidationException("error.validation.codesgroup"));
            ValidatorUtil.checkForNull(validatable.getTila(), new KoodistoValidationException("error.validation.tila"));
            ValidatorUtil.checkForBlank(validatable.getOrganisaatioOid(), new KoodistoValidationException("error.validation.organization"));
            checkMetadatas(validatable.getMetadata());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    @Override
    public void validateUpdate(KoodistoDto validatable) {
        try {
            ValidatorUtil.checkForNull(validatable, new KoodistoValidationException("error.validation.codes"));
            ValidatorUtil.checkForBlank(validatable.getKoodistoUri(), new KoodistoUriEmptyException());
            checkMetadatas(validatable.getMetadata());
        } catch (Exception e) {
            throw new KoodistoValidationException(e.getMessage(), e);
        }
    }

    private void checkRequiredMetadataFields(Collection<KoodistoMetadata> metadatas) {
        for (KoodistoMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), new KoodistoValidationException("error.validation.metadata"));
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoNimiEmptyException());
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException());
        checkRequiredMetadataFields(metadatas);
    }

}
