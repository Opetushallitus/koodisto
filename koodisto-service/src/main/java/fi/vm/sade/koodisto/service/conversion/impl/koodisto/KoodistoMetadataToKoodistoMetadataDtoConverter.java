package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import org.springframework.stereotype.Component;

@Component
public class KoodistoMetadataToKoodistoMetadataDtoConverter implements
        AbstractFromDomainConverter<KoodistoMetadata, KoodistoMetadataDto> {

    @Override
    public KoodistoMetadataDto convert(KoodistoMetadata source) {
        return KoodistoMetadataDto.builder()
                .kieli(source.getKieli())
                .nimi(source.getNimi())
                .kuvaus(source.getKuvaus())
                .build();
    }

}