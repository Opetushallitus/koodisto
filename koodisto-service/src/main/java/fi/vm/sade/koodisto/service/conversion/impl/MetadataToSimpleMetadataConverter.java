package fi.vm.sade.koodisto.service.conversion.impl;

import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoMetadata;

import java.util.ArrayList;
import java.util.List;

public class MetadataToSimpleMetadataConverter {

    
    public static SimpleMetadataDto convert(KoodistoMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus());
    }
    
    public static SimpleMetadataDto convert(KoodiMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus());
    }
    
    public static SimpleKoodiMetadataDto convertToSimpleKoodiMetadata(KoodiMetadata metadata) {
        return new SimpleKoodiMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus(), metadata.getLyhytNimi());
    }
    
    public static List<SimpleKoodiMetadataDto> convertToSimpleKoodiMetadata(KoodiMetadata ... koodiMetadatas) {
        List<SimpleKoodiMetadataDto> metadatas = new ArrayList<>();
        for (KoodiMetadata md : koodiMetadatas) {
            metadatas.add(new SimpleKoodiMetadataDto(md.getNimi(), md.getKieli(), md.getKuvaus(), md.getLyhytNimi()));
        }
        return metadatas;
    }
}
