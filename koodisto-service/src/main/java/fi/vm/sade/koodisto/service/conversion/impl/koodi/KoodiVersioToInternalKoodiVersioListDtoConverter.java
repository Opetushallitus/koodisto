package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioListDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioToInternalKoodiVersioListDtoConverter implements
        AbstractFromDomainConverter<KoodiVersio, InternalKoodiVersioListDto> {
    private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    @Override
    public InternalKoodiVersioListDto convert(KoodiVersio source) {
        return InternalKoodiVersioListDto.builder()
                .koodiArvo(source.getKoodiarvo())
                .versio(source.getVersio())
                .tila(source.getTila())
                .koodiUri(source.getKoodi().getKoodiUri())
                .paivitysPvm(source.getPaivitysPvm())
                .paivittajaOid(source.getPaivittajaOid())
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                .metadata(source.getMetadatas().stream()
                        .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }
}
