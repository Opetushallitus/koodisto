package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoPageDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioToInternalKoodiVersioDtoConverter implements
        AbstractFromDomainConverter<KoodiVersio, InternalKoodiVersioDto> {
    private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    private final KoodistoVersioToInternalKoodistoPageDtoConverter koodistoVersioToInternalKoodistoPageDtoConverter;

    @Override
    public InternalKoodiVersioDto convert(KoodiVersio source) {

        InternalKoodistoPageDto koodisto = source.getKoodistoVersios().stream()
                .filter(versio -> Objects.equals(versio.getKoodiVersio().getId(), source.getId()) && Objects.equals(versio.getKoodiVersio().getVersio(), source.getVersio()))
                .map(KoodistoVersioKoodiVersio::getKoodistoVersio)
                .min(Comparator.comparing(KoodistoVersio::getVersio))
                .map(koodistoVersioToInternalKoodistoPageDtoConverter::convert)
                .orElseThrow(KoodistoNotFoundException::new);

        return
                InternalKoodiVersioDto.builder()
                        .koodisto(koodisto)
                        .koodiArvo(source.getKoodiarvo())
                        .versio(source.getVersio())
                        .koodiVersio(source.getKoodi().getKoodiVersios().stream().map(KoodiVersio::getVersio).collect(Collectors.toList()))
                        .lockingVersion(source.getVersion())
                        .tila(source.getTila())
                        .koodiUri(source.getKoodi().getKoodiUri())
                        .paivitysPvm(source.getPaivitysPvm())
                        .paivittajaOid(source.getPaivittajaOid())
                        .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                        .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                        .metadata(source.getMetadatas().stream()
                                .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                                .collect(Collectors.toList()))
                        .rinnastuuKoodeihin(KoodiConverterUtil.getLevelsWithCodes(source))
                        .sisaltaaKoodit(KoodiConverterUtil.extractBySuhde(source.getAlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getAlakoodiVersio))
                        .sisaltyyKoodeihin(KoodiConverterUtil.extractBySuhde(source.getYlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getYlakoodiVersio))
                        .build();
    }
}
