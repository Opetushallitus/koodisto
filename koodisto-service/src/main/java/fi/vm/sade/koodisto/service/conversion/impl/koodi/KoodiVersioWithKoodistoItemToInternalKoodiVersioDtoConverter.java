package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.ExtendedConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioWithKoodistoItemToInternalKoodiVersioDtoConverter implements
        ExtendedConverter<KoodiVersioWithKoodistoItem, InternalKoodiVersioDto> {
    private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;

    @Override
    public InternalKoodiVersioDto convert(KoodiVersioWithKoodistoItem source) {
        return
                InternalKoodiVersioDto.builder()
                        .koodistoUri(source.getKoodistoItem().getKoodistoUri())
                        .koodiArvo(source.getKoodiVersio().getKoodiarvo())
                        .versio(source.getKoodiVersio().getVersio())
                        .lockingVersion(source.getKoodiVersio().getVersion())
                        .tila(source.getKoodiVersio().getTila())
                        .koodiUri(source.getKoodiVersio().getKoodi().getKoodiUri())
                        .paivitysPvm(source.getKoodiVersio().getPaivitysPvm())
                        .paivittajaOid(source.getKoodiVersio().getPaivittajaOid())
                        .voimassaAlkuPvm(source.getKoodiVersio().getVoimassaAlkuPvm())
                        .voimassaLoppuPvm(source.getKoodiVersio().getVoimassaLoppuPvm())
                        .metadata(source.getKoodiVersio().getMetadatas().stream()
                                .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                                .collect(Collectors.toList()))
                        .rinnastuuKoodeihin(KoodiConverterUtil.getLevelsWithCodes(source.getKoodiVersio()))
                        .sisaltaaKoodit(KoodiConverterUtil.extractBySuhde(source.getKoodiVersio().getAlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getAlakoodiVersio))
                        .sisaltyyKoodeihin(KoodiConverterUtil.extractBySuhde(source.getKoodiVersio().getYlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getYlakoodiVersio))
                        .build();
    }




}
