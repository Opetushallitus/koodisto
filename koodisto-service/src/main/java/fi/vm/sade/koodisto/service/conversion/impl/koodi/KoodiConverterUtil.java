package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.internal.InternalKoodiSuhdeDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KoodiConverterUtil {
    private KoodiConverterUtil() {

    }

    public static List<InternalKoodiSuhdeDto> getLevelsWithCodes(KoodiVersio koodiVersio) {
        List<InternalKoodiSuhdeDto> levelsWith = new ArrayList<>();
        levelsWith.addAll(extractBySuhde(koodiVersio.getYlakoodis(), SuhteenTyyppi.RINNASTEINEN, KoodinSuhde::getYlakoodiVersio));
        levelsWith.addAll(extractBySuhde(koodiVersio.getAlakoodis(), SuhteenTyyppi.RINNASTEINEN, KoodinSuhde::getAlakoodiVersio));
        return levelsWith;
    }

    public static List<InternalKoodiSuhdeDto> extractBySuhde(Set<KoodinSuhde> source, SuhteenTyyppi tyyppi,
                                                             Function<KoodinSuhde, KoodiVersio> getKoodiVersio) {
        return source.stream()
                .filter(ks -> ks.getSuhteenTyyppi() == tyyppi)
                .map(getKoodiVersio)
                .filter(Objects::nonNull)
                .map(relatedKoodi -> (InternalKoodiSuhdeDto.builder()
                        .koodiUri(relatedKoodi.getKoodi().getKoodiUri())
                        .koodiVersio(relatedKoodi.getVersio())
                        .nimi(relatedKoodi.getNimi())
                        .kuvaus(relatedKoodi.getKuvaus())
                        .koodistoNimi(relatedKoodi.getKoodistoNimi())
                        .build())).collect(Collectors.toList());
    }
}
