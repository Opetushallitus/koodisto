package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractToDomainConverter;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaMetadataDtoToKoodistoRyhmaMetadataConverter")
public class KoodistoRyhmaMetadataDtoToKoodistoRyhmaMetadataConverter implements
        AbstractToDomainConverter<KoodistoRyhmaMetadataDto, KoodistoRyhmaMetadata> {

    @Override
    public KoodistoRyhmaMetadata convert(final KoodistoRyhmaMetadataDto dto) {
        KoodistoRyhmaMetadata metadata = KoodistoRyhmaMetadata
                .builder()
                .kieli(dto.getKieli())
                .nimi(dto.getNimi())
                .build();
        metadata.setId(dto.getId());
        return metadata;
    }
}
