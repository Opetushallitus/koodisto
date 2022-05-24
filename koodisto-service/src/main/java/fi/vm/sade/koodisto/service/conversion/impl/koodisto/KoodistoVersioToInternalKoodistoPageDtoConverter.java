package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodisuhdeDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioToInternalKoodiVersioDtoConverter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KoodistoVersioToInternalKoodistoPageDtoConverter extends
        AbstractFromDomainConverter<KoodistoVersio, InternalKoodistoPageDto> {
    private final KoodistoMetadataToKoodistoMetadataDtoConverter koodistoMetadataToKoodistoMetadataDtoConverter;
    private final KoodiVersioToInternalKoodiVersioDtoConverter koodiVersioToInternalKoodiVersioDtoConverter;

    @Override
    public InternalKoodistoPageDto convert(KoodistoVersio source) {
        List<InternalKoodisuhdeDto> levelsWithCodes = getLevelsWithCodes(source);
        return InternalKoodistoPageDto.builder()
                .resourceUri(source.getKoodisto().getKoodistoUri())
                .versio(source.getVersio())
                .paivitysPvm(source.getPaivitysPvm())
                .paivittajaOid(source.getPaivittajaOid())
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                .tila(source.getTila())
                .metadata(source.getMetadatas().stream()
                        .map(koodistoMetadataToKoodistoMetadataDtoConverter::convert)
                        .collect(Collectors.toList()))
                .koodiVersio(source.getKoodisto().getKoodistoVersios().stream()
                        .map(KoodistoVersio::getVersio)
                        .collect(Collectors.toList()))
                .rinnastuuKoodistoihin(levelsWithCodes)
                .sisaltaaKoodistot(extractBySuhde(source.getAlakoodistos(), SuhteenTyyppi.SISALTYY, KoodistonSuhde::getAlakoodistoVersio))
                .sisaltyyKoodistoihin(extractBySuhde(source.getYlakoodistos(), SuhteenTyyppi.SISALTYY, KoodistonSuhde::getYlakoodistoVersio))
                .koodiList(source.getKoodiVersios().stream()
                        .map(KoodistoVersioKoodiVersio::getKoodiVersio)
                        .map(koodiVersioToInternalKoodiVersioDtoConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }

    private static List<InternalKoodisuhdeDto> getLevelsWithCodes(KoodistoVersio source) {
        List<InternalKoodisuhdeDto> levelsWith = new ArrayList<>();
        levelsWith.addAll(extractBySuhde(source.getYlakoodistos(), SuhteenTyyppi.RINNASTEINEN, KoodistonSuhde::getYlakoodistoVersio));
        levelsWith.addAll(extractBySuhde(source.getAlakoodistos(), SuhteenTyyppi.RINNASTEINEN, KoodistonSuhde::getAlakoodistoVersio));
        return levelsWith;
    }


    private static List<InternalKoodisuhdeDto> extractBySuhde(Set<KoodistonSuhde> source, SuhteenTyyppi tyyppi,
                                                              Function<? super KoodistonSuhde, ? extends KoodistoVersio> getKoodistoVersio) {
        return source.stream()
                .filter(ks -> ks.getSuhteenTyyppi() == tyyppi)
                .map(getKoodistoVersio)
                .filter(Objects::nonNull)
                .map(relatedKoodisto -> (InternalKoodisuhdeDto.builder()
                        .koodiUri(relatedKoodisto.getKoodisto().getKoodistoUri())
                        .koodiVersio(relatedKoodisto.getVersio())
                        .nimi(getNimi(relatedKoodisto))
                        .kuvaus(getKuvaus(relatedKoodisto)).build())).collect(Collectors.toList());
    }

    private static Map<String, String> getNimi(KoodistoVersio koodistoVersio) {
        return koodistoVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodistoMetadata::getNimi));
    }

    private static Map<String, String> getKuvaus(KoodistoVersio koodistoVersio) {
        return koodistoVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), koodistoMetadata -> Optional.ofNullable(koodistoMetadata.getKuvaus()).orElse("")));
    }
}
