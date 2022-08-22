package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoSuhdeDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaMissingException;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioToInternalKoodiVersioDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.properties.OphProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodistoVersioToInternalKoodistoPageDtoConverter implements
        AbstractFromDomainConverter<KoodistoVersio, InternalKoodistoPageDto> {

    private final OphProperties ophProperties;
    private final KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter koodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter;
    private final KoodistoMetadataToKoodistoMetadataDtoConverter koodistoMetadataToKoodistoMetadataDtoConverter;

    private final List<Kieli> languageSortOrder = List.of(Kieli.FI, Kieli.SV, Kieli.EN);

    @Override
    public InternalKoodistoPageDto convert(KoodistoVersio source) {
        return InternalKoodistoPageDto.builder()
                .koodistoRyhmaUri(source.getKoodisto().getKoodistoRyhmas().stream().findFirst().orElseThrow(KoodistoRyhmaMissingException::new).getKoodistoRyhmaUri())
                .koodistoRyhmaMetadata(source.getKoodisto().getKoodistoRyhmas().stream()
                        .findFirst()
                        .map(KoodistoRyhma::getKoodistoRyhmaMetadatas)
                        .map(a -> a.stream()
                                .map(koodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter::convert)
                                .collect(Collectors.toSet()))
                        .orElse(null))
                .resourceUri(MessageFormat.format(ophProperties.url("koodistoUriFormat"), source.getKoodisto().getKoodistoUri()))
                .koodistoUri(source.getKoodisto().getKoodistoUri())
                .versio(source.getVersio())
                .lockingVersion(source.getVersion())
                .organisaatioOid(source.getKoodisto().getOrganisaatioOid())
                .paivitysPvm(source.getPaivitysPvm())
                .paivittajaOid(source.getPaivittajaOid())
                .voimassaAlkuPvm(source.getVoimassaAlkuPvm())
                .voimassaLoppuPvm(source.getVoimassaLoppuPvm())
                .tila(TilaType.valueOf(source.getTila().name()))
                .omistaja(source.getKoodisto().getOmistaja())
                .metadata(source.getMetadatas().stream()
                        .map(koodistoMetadataToKoodistoMetadataDtoConverter::convert)
                        .sorted(((a, b) -> a != null && a.equals(b) ? 0 : languageSortOrder.indexOf(a.getKieli()) < languageSortOrder.indexOf(b.getKieli()) ? -1 : 1))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .koodistoVersio(source.getKoodisto().getKoodistoVersios().stream()
                        .map(KoodistoVersio::getVersio)
                        .collect(Collectors.toList()))
                .rinnastuuKoodistoihin(getLevelsWithCodes(source))
                .sisaltaaKoodistot(extractBySuhde(source.getAlakoodistos(), SuhteenTyyppi.SISALTYY, KoodistonSuhde::getAlakoodistoVersio))
                .sisaltyyKoodistoihin(extractBySuhde(source.getYlakoodistos(), SuhteenTyyppi.SISALTYY, KoodistonSuhde::getYlakoodistoVersio))
                .build();
    }

    private static List<InternalKoodistoSuhdeDto> getLevelsWithCodes(KoodistoVersio source) {
        List<InternalKoodistoSuhdeDto> levelsWith = new ArrayList<>();
        levelsWith.addAll(extractBySuhde(source.getYlakoodistos(), SuhteenTyyppi.RINNASTEINEN, KoodistonSuhde::getYlakoodistoVersio));
        levelsWith.addAll(extractBySuhde(source.getAlakoodistos(), SuhteenTyyppi.RINNASTEINEN, KoodistonSuhde::getAlakoodistoVersio));
        return levelsWith;
    }


    private static List<InternalKoodistoSuhdeDto> extractBySuhde(Set<KoodistonSuhde> source, SuhteenTyyppi tyyppi,
                                                                 Function<KoodistonSuhde, KoodistoVersio> getKoodistoVersio) {
        return source.stream()
                .filter(ks -> ks.getSuhteenTyyppi() == tyyppi)
                .map(getKoodistoVersio)
                .filter(Objects::nonNull)
                .map(relatedKoodisto -> (InternalKoodistoSuhdeDto.builder()
                        .koodistoUri(relatedKoodisto.getKoodisto().getKoodistoUri())
                        .koodistoVersio(relatedKoodisto.getVersio())
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
