package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
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
        converted.setKoodistoUri(source.getKoodisto().getKoodistoUri());

        for (KoodistonSuhde ks : source.getYlakoodistos()) {
            if (ks.getYlakoodistoVersio() != null) {
                KoodistoVersio relatedKoodisto = ks.getYlakoodistoVersio();
                switch (ks.getSuhteenTyyppi()) {
                    case RINNASTEINEN:
                        converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(),
                                relatedKoodisto.getVersio(),
                                ks.isPassive(),
                                this.getNimi(relatedKoodisto)));
                        break;
                    case SISALTYY:
                        converted.getWithinCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto)));
                        break;
                }
            }
        }

        for (KoodistonSuhde ks : source.getAlakoodistos()) {
            if (ks.getAlakoodistoVersio() != null) {
                KoodistoVersio relatedKoodisto = ks.getAlakoodistoVersio();
                switch (ks.getSuhteenTyyppi()) {
                    case RINNASTEINEN:
                        converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto)));
                        break;
                    case SISALTYY:
                        converted.getIncludesCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive(), this.getNimi(relatedKoodisto)));
                        break;
                }
            }
        }

        if (!Strings.isNullOrEmpty(converted.getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodistoUri"), converted.getKoodistoUri());
            converted.setResourceUri(resourceUri);
        }
        converted.setOmistaja(source.getKoodisto().getOmistaja());
        converted.setOrganisaatioOid(source.getKoodisto().getOrganisaatioOid());
        converted.setLukittu(source.getKoodisto().getLukittu());

        converted.setPaivitysPvm(source.getPaivitysPvm());
        converted.setPaivittajaOid(source.getPaivittajaOid());
        converted.setTila(source.getTila());
        converted.setVersio(source.getVersio());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        converted.setVersion(source.getVersion());
        String uri = "";
        for (KoodistoRyhma ryhma : source.getKoodisto().getKoodistoRyhmas()) {
            if (uri.isEmpty() || !ryhma.getKoodistoRyhmaUri().contains("kaikki")) {
                uri = ryhma.getKoodistoRyhmaUri();
            }
        }
        converted.setCodesGroupUri(uri);
        converted.getMetadata().addAll(source.getMetadatas());
        List<Integer> codesVersions = new ArrayList<>();
        if (source.getKoodisto().getKoodistoVersios() != null) {
            for (KoodistoVersio koodistoVersio : source.getKoodisto().getKoodistoVersios()) {
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
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name(), KoodistoMetadata::getNimi));
    }
}
