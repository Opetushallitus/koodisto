package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Collection;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesGroupValidator implements RestValidator<KoodistoRyhmaDto> {

    @Override
    public void validateInsert(KoodistoRyhmaDto validatable) {
        ValidatorUtil.checkForNull(validatable, "No " + KoodistoRyhmaDto.class.getSimpleName() + " given while inserting codesgroup");
        checkMetadatas(validatable.getKoodistoRyhmaMetadatas());
    }

    @Override
    public void validateUpdate(KoodistoRyhmaDto validatable) {
        ValidatorUtil.checkForNull(validatable, "No " + KoodistoRyhmaDto.class.getSimpleName() + " given while updating codesgroup");
        ValidatorUtil.checkForBlank(validatable.getKoodistoRyhmaUri(), new KoodistoRyhmaUriEmptyException("codesgroup.uri.is.empty"));
    }

    @Override
    public void validateDelete(String uri, Integer version) {
        ValidatorUtil.checkForGreaterThan(version, 0, new IllegalArgumentException("Invalid parameter for deleting codesgroup, id: " + version));
    }

    @Override
    public void validateGet(String uri) {
        ValidatorUtil.checkForGreaterThan(Integer.valueOf(uri), 0, new IllegalArgumentException("Invalid parameter for fetching codesgroup, id: " + uri));
    }
    
    private void checkRequiredMetadataFields(Collection<KoodistoRyhmaMetadata> metadatas) {
        for (KoodistoRyhmaMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), "No language defined for metadata");
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoRyhmaNimiEmptyException("No koodistoryhma nimi defined for language " + md.getKieli().name()));
        }
    }

    private void checkMetadatas(Collection<KoodistoRyhmaMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException("codes.metadata.is.empty"));
        checkRequiredMetadataFields(metadatas);
    }

}
