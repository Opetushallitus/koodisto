package fi.vm.sade.koodisto.service.koodisto.rest.validator;


import java.util.Collection;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidator implements RestValidator<KoodistoDto> {

    @Override
    public void validateCreateNew(KoodistoDto validatable) {
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
    public void validateDelete(KoodistoDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Codes given was null");
        
    }

    @Override
    public void validateGet(KoodistoDto validatable) {
        // TODO Auto-generated method stub
        
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
