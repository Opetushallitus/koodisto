package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiSuhdeDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
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
                        .koodistoUri(source.getKoodistoVersios().stream()
                                .map(KoodistoVersioKoodiVersio::getKoodistoVersio)
                                .reduce((a, b) -> a.getVersio() > b.getVersio() ? a : b)
                                .orElseThrow(KoodistoNotFoundException::new).getKoodisto().getKoodistoUri()
                        )
                        .koodiArvo(source.getKoodiarvo())
                        .versio(source.getVersio())
                        .koodiUri(source.getKoodi().getKoodiUri())
                        .paivitysPvm(source.getPaivitysPvm())
                        .paivittajaOid(source.getPaivittajaOid())
                        .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                        .metadata(source.getMetadatas().stream()
                                .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                                .collect(Collectors.toList()))
                        .rinnastuuKoodeihin(getLevelsWithCodes(source))
                        .sisaltaaKoodit(extractBySuhde(source.getAlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getAlakoodiVersio))
                        .sisaltyyKoodeihin(extractBySuhde(source.getYlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getYlakoodiVersio))
                        .build();
    }

    private static List<InternalKoodiSuhdeDto> getLevelsWithCodes(KoodiVersio source) {
        List<InternalKoodiSuhdeDto> levelsWith = new ArrayList<>();
        levelsWith.addAll(extractBySuhde(source.getYlakoodis(), SuhteenTyyppi.RINNASTEINEN, KoodinSuhde::getYlakoodiVersio));
        levelsWith.addAll(extractBySuhde(source.getAlakoodis(), SuhteenTyyppi.RINNASTEINEN, KoodinSuhde::getAlakoodiVersio));
        return levelsWith;
    }


    private static List<InternalKoodiSuhdeDto> extractBySuhde(Set<KoodinSuhde> source, SuhteenTyyppi tyyppi,
                                                              Function<KoodinSuhde, KoodiVersio> getKoodiVersio) {
        return source.stream()
                .filter(ks -> ks.getSuhteenTyyppi() == tyyppi)
                .map(getKoodiVersio)
                .filter(Objects::nonNull)
                .map(relatedKoodi -> (InternalKoodiSuhdeDto.builder()
                        .koodiUri(relatedKoodi.getKoodi().getKoodiUri())
                        .koodiVersio(relatedKoodi.getVersio())
                        .nimi(getNimi(relatedKoodi))
                        .kuvaus(getKuvaus(relatedKoodi))
                        .koodistoNimi(getKoodistoNimi(relatedKoodi))
                        .build())).collect(Collectors.toList());
    }

    private static Map<String, String> getKoodistoNimi(KoodiVersio koodiVersio) {
        return koodiVersio.getKoodi().getKoodisto().getKoodistoVersios().stream()
                .reduce((a, b) -> a.getVersio() > b.getVersio() ? a : b)
                .orElseThrow(KoodistoNotFoundException::new)
                .getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodistoMetadata::getNimi));
    }

    private static Map<String, String> getNimi(KoodiVersio koodiVersio) {
        return koodiVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodiMetadata::getNimi));
    }

    private static Map<String, String> getKuvaus(KoodiVersio koodiVersio) {
        return koodiVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), koodiMetadata -> Optional.ofNullable(koodiMetadata.getKuvaus()).orElse("")));
    }
}
