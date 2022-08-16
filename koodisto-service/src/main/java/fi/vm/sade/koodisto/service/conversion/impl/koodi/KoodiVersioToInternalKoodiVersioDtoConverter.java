package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoPageDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioToInternalKoodiVersioDtoConverter implements
        AbstractFromDomainConverter<KoodiVersio, InternalKoodiVersioDto> {
    private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    private final KoodistoVersioToInternalKoodistoPageDtoConverter koodistoVersioToInternalKoodistoPageDtoConverter;

    @Override
    public InternalKoodiVersioDto convert(KoodiVersio source) {
        return
                InternalKoodiVersioDto.builder()
                        .koodisto(source.getKoodistoVersios().stream()
                                .map(KoodistoVersioKoodiVersio::getKoodistoVersio)
                                .map(koodistoVersioToInternalKoodistoPageDtoConverter::convert)
                                .reduce((a, b) -> a.getVersio() > b.getVersio() ? a : b)
                                .orElseThrow(KoodistoNotFoundException::new)
                        )
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
