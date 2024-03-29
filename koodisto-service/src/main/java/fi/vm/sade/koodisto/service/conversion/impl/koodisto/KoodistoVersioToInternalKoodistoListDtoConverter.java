package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoListDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodistoVersioToInternalKoodistoListDtoConverter implements
        AbstractFromDomainConverter<KoodistoVersio, InternalKoodistoListDto> {
    private final KoodistoMetadataToKoodistoMetadataDtoConverter koodistoMetadataToKoodistoMetadataDtoConverter;
    private final KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter koodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter;

    @Override
    public InternalKoodistoListDto convert(KoodistoVersio source) {
        return InternalKoodistoListDto.builder()
                .koodistoUri(source.getKoodisto().getKoodistoUri())
                .versio(source.getVersio())
                .koodistoRyhmaMetadata(source.getKoodisto().getKoodistoRyhmas().stream()
                        .findFirst()
                        .map(KoodistoRyhma::getKoodistoRyhmaMetadatas)
                        .map(a -> a.stream()
                                .map(koodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter::convert)
                                .collect(Collectors.toSet()))
                        .orElse(null))
                .metadata(source.getMetadatas().stream()
                        .map(koodistoMetadataToKoodistoMetadataDtoConverter::convert)
                        .collect(Collectors.toList()))
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                .koodiCount(source.getKoodiCount())
                .build();
    }
}
