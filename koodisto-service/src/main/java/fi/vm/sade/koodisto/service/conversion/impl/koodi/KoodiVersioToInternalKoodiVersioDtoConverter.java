package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioToInternalKoodiVersioDtoConverter implements
        AbstractFromDomainConverter<KoodiVersio, InternalKoodiVersioDto> {
private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    @Override
    public InternalKoodiVersioDto convert(KoodiVersio source) {
        return
        InternalKoodiVersioDto.builder()
                .koodiArvo(source.getKoodiarvo())
                .versio(source.getVersio())
                .koodiUri(source.getKoodi().getKoodiUri())
                .paivitysPvm(source.getPaivitysPvm())
                .paivittajaOid(source.getPaivittajaOid())
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .metadata(source.getMetadatas().stream()
                        .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }
}
