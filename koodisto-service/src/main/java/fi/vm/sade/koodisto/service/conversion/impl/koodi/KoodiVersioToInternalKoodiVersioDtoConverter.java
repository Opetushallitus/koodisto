package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KoodiVersioToInternalKoodiVersioDtoConverter extends
        AbstractFromDomainConverter<KoodiVersio, InternalKoodiVersioDto> {
private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    @Override
    public InternalKoodiVersioDto convert(KoodiVersio source) {
        return
        InternalKoodiVersioDto.builder()
                .koodiArvo(source.getKoodiarvo())
                .versio(source.getVersio())
                .paivitysPvm(source.getPaivitysPvm())
                .paivittajaOid(source.getPaivittajaOid())
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .metadata(source.getMetadatas().stream()
                        .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }
}
