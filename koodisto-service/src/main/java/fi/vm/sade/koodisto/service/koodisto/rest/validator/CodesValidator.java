package fi.vm.sade.koodisto.service.koodisto.rest.validator;


import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidator implements RestValidator<KoodistoDto> {

    @Override
    public void validateCreateNew(KoodistoDto validatable) {
        ValidatorUtil.checkForNull(validatable, "Codes given was null");
        ValidatorUtil.checkForNullOrEmpty(validatable.getCodesGroupUri(), "No codes group given");
        ValidatorUtil.checkForNullOrEmpty(validatable.getKoodistoUri(), "No codes uri is given");
        ValidatorUtil.checkForNull(validatable.getTila(), "No Tila is given");
        checkMetadatas(validatable.getMetadata());
    }

    @Override
    public void validateUpdate(KoodistoDto validatable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void validateDelete(KoodistoDto validatable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void validateGet(KoodistoDto validatable) {
        // TODO Auto-generated method stub
        
    }
    
    private void checkRequiredMetadataFields(Collection<KoodistoMetadata> metadatas) {
        for (KoodistoMetadata md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
                throw new KoodistoNimiEmptyException("No koodisto nimi defined for language " + md.getKieli().name());
            } else if (StringUtils.isBlank(md.getKuvaus())) {
                throw new KoodistoKuvausEmptyException("No koodisto kuvaus defined for language " + md.getKieli().name());
            }
        }
    }

    private void checkMetadatas(Collection<KoodistoMetadata> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException("codes.metadata.is.empty");
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

}
