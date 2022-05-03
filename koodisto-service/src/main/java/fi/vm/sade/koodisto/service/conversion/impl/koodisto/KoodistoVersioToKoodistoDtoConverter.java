package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KoodistoVersioToKoodistoDtoConverter extends AbstractFromDomainConverter<KoodistoVersio, KoodistoDto> {
    private OphProperties ophProperties;

    public KoodistoVersioToKoodistoDtoConverter(OphProperties ophProperties) {
        this.ophProperties = ophProperties;
    }

    @Override
    public KoodistoDto convert(KoodistoVersio source) {

        KoodistoDto converted = new KoodistoDto();
        Koodisto sourceKoodisto = source.getKoodisto();
        converted.setKoodistoUri(sourceKoodisto.getKoodistoUri());
        source.getYlakoodistos().stream().filter(koodistonSuhde -> koodistonSuhde.getYlakoodistoVersio() != null).forEach(ks -> {
            KoodistoVersio relatedKoodisto = ks.getYlakoodistoVersio();
            switch (ks.getSuhteenTyyppi()) {
                case RINNASTEINEN:
                    converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(),
                            relatedKoodisto.getVersio(),
                            ks.isPassive(),
                            this.getNimi(relatedKoodisto),
                            this.getKuvaus(relatedKoodisto)));
                    break;
                case SISALTYY:
                    converted.getWithinCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto), this.getKuvaus(relatedKoodisto)));
                    break;
            }
        });

        source.getAlakoodistos().stream().filter(koodistonSuhde -> koodistonSuhde.getAlakoodistoVersio() != null).forEach(ks -> {
            KoodistoVersio relatedKoodisto = ks.getAlakoodistoVersio();
            switch (ks.getSuhteenTyyppi()) {
                case RINNASTEINEN:
                    converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto), this.getKuvaus(relatedKoodisto)));
                    break;
                case SISALTYY:
                    converted.getIncludesCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto), this.getKuvaus(relatedKoodisto)));
                    break;
            }
        });

        if (!Strings.isNullOrEmpty(converted.getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodistoUriFormat"), converted.getKoodistoUri());
            converted.setResourceUri(resourceUri);
        }

        String ryhmatUri = sourceKoodisto.getKoodistoRyhmas().stream()
                .map(KoodistoRyhma::getKoodistoRyhmaUri)
                .filter(koodistoRyhmaUri -> !koodistoRyhmaUri.contains("kaikki"))
                .findAny().orElse(sourceKoodisto.getKoodistoRyhmas().stream().findFirst().toString());
        converted.setCodesGroupUri(ryhmatUri);

        converted.setOmistaja(sourceKoodisto.getOmistaja());
        converted.setOrganisaatioOid(sourceKoodisto.getOrganisaatioOid());
        converted.setLukittu(sourceKoodisto.getLukittu());

        converted.setPaivitysPvm(source.getPaivitysPvm());
        converted.setPaivittajaOid(source.getPaivittajaOid());
        converted.setTila(source.getTila());
        converted.setVersio(source.getVersio());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        converted.setVersion(source.getVersion());
        converted.getMetadata().addAll(source.getMetadatas());
        List<Integer> codesVersions = new ArrayList<>();
        if (sourceKoodisto.getKoodistoVersios() != null) {
            for (KoodistoVersio koodistoVersio : sourceKoodisto.getKoodistoVersios()) {
                if (!source.getVersio().equals(koodistoVersio.getVersio())) {
                    codesVersions.add(koodistoVersio.getVersio());
                }
            }
        }
        converted.setCodesVersions(codesVersions);

        return converted;
    }

    private Map<String, String> getNimi(KoodistoVersio koodistoVersio) {
        return koodistoVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodistoMetadata::getNimi));
    }

    private Map<String, String> getKuvaus(KoodistoVersio koodistoVersio) {
        return koodistoVersio.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodistoMetadata::getKuvaus));
    }
}
