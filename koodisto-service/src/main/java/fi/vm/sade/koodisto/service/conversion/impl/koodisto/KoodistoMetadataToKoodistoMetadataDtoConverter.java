package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;

public class KoodistoMetadataToKoodistoMetadataDtoConverter extends
        AbstractFromDomainConverter<KoodistoMetadata, KoodistoMetadataDto> {

    @Override
    public KoodistoMetadataDto convert(KoodistoMetadata source) {
        return KoodistoMetadataDto.builder()
                .kieli(source.getKieli())
                .nimi(source.getNimi())
                .build();
    }

}