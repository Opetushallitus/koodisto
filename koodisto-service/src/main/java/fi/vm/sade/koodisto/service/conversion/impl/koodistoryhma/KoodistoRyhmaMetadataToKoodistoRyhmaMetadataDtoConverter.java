package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import org.springframework.stereotype.Component;

@Component
public class KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter implements
        AbstractFromDomainConverter<KoodistoRyhmaMetadata, KoodistoRyhmaMetadataDto> {

    @Override
    public KoodistoRyhmaMetadataDto convert(KoodistoRyhmaMetadata source) {
        return KoodistoRyhmaMetadataDto.builder()
                .id(source.getKoodistoRyhma().getId())
                .uri(source.getKoodistoRyhma().getKoodistoRyhmaUri())
                .kieli(source.getKieli())
                .nimi(source.getNimi())
                .build();
    }
}
