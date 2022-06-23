package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("koodistoRyhmaToKoodistoRyhmaDtoConverter")
public class KoodistoRyhmaToKoodistoRyhmaDtoConverter implements AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaDto> {

    private final KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter metadataConverter;

    @Override
    public KoodistoRyhmaDto convert(KoodistoRyhma source) {

        KoodistoRyhmaDto converted = new KoodistoRyhmaDto();

        converted.setId(source.getId());
        converted.setKoodistoRyhmaUri(source.getKoodistoRyhmaUri());
        converted.setKoodistoRyhmaMetadatas(source.getKoodistoJoukkoMetadatas().stream()
                .map(metadataConverter::convert)
                .collect(Collectors.toSet()));
        converted.setKoodistos(source.getKoodistos());

        return converted;
    }
}
