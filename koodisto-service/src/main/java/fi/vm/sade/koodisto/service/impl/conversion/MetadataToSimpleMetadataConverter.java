package fi.vm.sade.koodisto.service.impl.conversion;

import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoMetadata;

public class MetadataToSimpleMetadataConverter {

    
    public static SimpleMetadataDto convert(KoodistoMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus());
    }
    
    public static SimpleMetadataDto convert(KoodiMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus());
    }
}
