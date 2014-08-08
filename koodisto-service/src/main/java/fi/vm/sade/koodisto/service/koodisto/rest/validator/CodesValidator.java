package fi.vm.sade.koodisto.service.koodisto.rest.validator;


import java.util.Collection;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersionNumberEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidator implements RestValidator<KoodistoDto> {

    @Override
    public void validateInsert(KoodistoDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Codes given was null");
        ValidatorUtil.checkForBlank(validatable.getCodesGroupUri(), "No codes group given");
        ValidatorUtil.checkForNull(validatable.getTila(), "No Tila is given");
        ValidatorUtil.checkForBlank(validatable.getOrganisaatioOid(), "No organization provided for codes");
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodistoDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Codes given was null");
        ValidatorUtil.checkForBlank(validatable.getKoodistoUri(), new KoodistoUriEmptyException("codes.uri.is.empty"));
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateDelete(String uri, Integer version) {
        ValidatorUtil.checkForBlank(uri, new KoodistoUriEmptyException("codes.uri.is.empty"));
        ValidatorUtil.checkForNull(version, new KoodistoVersionNumberEmptyException("Koodisto version number is empty"));
    }

    @Override
    public void validateGet(String uri) {
        ValidatorUtil.checkForBlank(uri, new KoodistoUriEmptyException("codes.uri.is.empty"));
    }
    
    private void checkRequiredMetadataFields(Collection<KoodistoMetadata> metadatas) {
        for (KoodistoMetadata md : metadatas) {
            ValidatorUtil.checkForNull(md.getKieli(), "No language defined for metadata");
            ValidatorUtil.checkForBlank(md.getNimi(), new KoodistoNimiEmptyException("No koodisto nimi defined for language " + md.getKieli().name()));
            ValidatorUtil.checkForBlank(md.getKuvaus(), new KoodistoKuvausEmptyException("No koodisto kuvaus defined for language " + md.getKieli().name()));
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadata> metadatas) {
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(metadatas, new MetadataEmptyException("codes.metadata.is.empty"));
        checkRequiredMetadataFields(metadatas);
    }
    

}
