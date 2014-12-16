package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;

@Component("koodistoVersioToKoodistoDtoConverter")
public class KoodistoVersioToKoodistoDtoConverter extends AbstractFromDomainConverter<KoodistoVersio, KoodistoDto> {

    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    @Override
    public KoodistoDto convert(KoodistoVersio source) {

        KoodistoDto converted = new KoodistoDto();
        converted.setKoodistoUri(source.getKoodisto().getKoodistoUri());

        for(KoodistonSuhde ks : source.getYlakoodistos()) {
            if (ks.getYlakoodistoVersio() != null) {
                KoodistoVersio relatedKoodisto = ks.getYlakoodistoVersio();
                switch (ks.getSuhteenTyyppi()) {
                case RINNASTEINEN:
                    converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive()));
                    break;
                case SISALTYY: 
                    converted.getWithinCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive()));
                    break;
                }
            }
        }
        for(KoodistonSuhde ks : source.getAlakoodistos()) {
            if (ks.getAlakoodistoVersio() != null) {
                KoodistoVersio relatedKoodisto = ks.getAlakoodistoVersio();
                switch (ks.getSuhteenTyyppi()) {
                case RINNASTEINEN:
                    converted.getLevelsWithCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive()));
                    break;
                case SISALTYY: 
                    converted.getIncludesCodes().add(new RelationCodes(relatedKoodisto.getKoodisto().getKoodistoUri(), relatedKoodisto.getVersio(), ks.isPassive()));
                    break;
                }
            }
        }



        if (StringUtils.isNotBlank(converted.getKoodistoUri())) {
            converted.setResourceUri(koodistoConfiguration.getKoodistoResourceUri(converted.getKoodistoUri()));
        }
        converted.setOmistaja(source.getKoodisto().getOmistaja());
        converted.setOrganisaatioOid(source.getKoodisto().getOrganisaatioOid());
        converted.setLukittu(source.getKoodisto().getLukittu());

        converted.setPaivitysPvm(source.getPaivitysPvm());
        converted.setTila(source.getTila());
        converted.setVersio(source.getVersio());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        converted.setVersion(source.getVersion());
        String uri = "";
        for (KoodistoRyhma ryhma : source.getKoodisto().getKoodistoRyhmas()) {
            if (uri.isEmpty() || ryhma.getKoodistoRyhmaUri().indexOf("kaikki") == -1) {
                uri = ryhma.getKoodistoRyhmaUri();
            }
        }
        converted.setCodesGroupUri(uri);
        converted.getMetadata().addAll(source.getMetadatas());
        List<Integer> codesVersions = new ArrayList<Integer>();
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
}
