package fi.vm.sade.koodisto.service.impl.conversion;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoMetadata;

public class MetadataToSimpleMetadataConverter {

    
    public static SimpleMetadataDto convert(KoodistoMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus(), null, null);
    }
    
    public static SimpleMetadataDto convert(KoodiMetadata metadata) {
        return new SimpleMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus(), metadata.getAlkuPvm(), metadata.getLoppuPvm());
    }
    
    public static SimpleKoodiMetadataDto convertToSimpleKoodiMetadata(KoodiMetadata metadata) {
        return new SimpleKoodiMetadataDto(metadata.getNimi(), metadata.getKieli(), metadata.getKuvaus(), metadata.getLyhytNimi(), metadata.getAlkuPvm(), metadata.getLoppuPvm());
    }
    
    public static List<SimpleKoodiMetadataDto> convertToSimpleKoodiMetadata(KoodiMetadata ... koodiMetadatas) {
        List<SimpleKoodiMetadataDto> metadatas = new ArrayList<>();
        for (KoodiMetadata md : koodiMetadatas) {
            metadatas.add(new SimpleKoodiMetadataDto(md.getNimi(), md.getKieli(), md.getKuvaus(), md.getLyhytNimi(), md.getAlkuPvm(), md.getLoppuPvm()));
        }
        return metadatas;
    }
}
