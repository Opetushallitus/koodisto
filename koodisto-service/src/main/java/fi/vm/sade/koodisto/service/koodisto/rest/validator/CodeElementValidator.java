package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodiKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiLyhytNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodeElementValidator implements RestValidator<KoodiDto> {

    @Override
    public void validateInsert(KoodiDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Code element given was null");
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodiDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Code element given was null");
        ValidatorUtil.checkForBlank(validatable.getKoodiUri(), new KoodiUriEmptyException("codeelement.uri.is.empty"));
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateDelete(String uri, Integer version) {
        ValidatorUtil.checkForBlank(uri, new KoodiUriEmptyException("codeelement.uri.is.empty"));
        ValidatorUtil.checkForGreaterThan(version, 0, new IllegalArgumentException("Incorrect version: " + version));
    }

    @Override
    public void validateGet(String uri) {
        ValidatorUtil.checkForBlank(uri, new KoodiUriEmptyException("codeelement.uri.is.empty"));
    }
    
    private void checkRequiredMetadataFields(Collection<KoodiMetadata> metadatas) {
        for (KoodiMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), "No language defined for metadata");
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodiNimiEmptyException("No koodi nimi defined for language " + md.getKieli().name()));
            ValidatorUtil.checkForBlank(md.getKuvaus(), new KoodiKuvausEmptyException("No koodi kuvaus defined for language " + md.getKieli().name()));
            ValidatorUtil.checkForBlank(md.getLyhytNimi(), new KoodiLyhytNimiEmptyException("No koodi lyhyt nimi defined for language " + md.getKieli().name()));
        }
    }

    private void checkMetadatas(Collection<KoodiMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException("codeelement.metadata.is.empty"));
        checkRequiredMetadataFields(metadatas);
    }

}
