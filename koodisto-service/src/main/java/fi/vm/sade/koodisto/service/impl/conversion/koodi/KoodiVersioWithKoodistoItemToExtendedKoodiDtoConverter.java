package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Iterator;


@Component("koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter")
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, ExtendedKoodiDto> {

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

    public void setKoodistoConfiguration(KoodistoConfiguration koodistoConfiguration) {
        this.koodistoConfiguration = koodistoConfiguration;
    }

    @Override
    public ExtendedKoodiDto convert(KoodiVersioWithKoodistoItem source) {
        ExtendedKoodiDto converted = new ExtendedKoodiDto();

        converted.setKoodiArvo(source.getKoodiVersio().getKoodiarvo());
        converted.setKoodiUri(source.getKoodiVersio().getKoodi().getKoodiUri());
        if (source.getKoodiVersio().getYlakoodis() != null && source.getKoodiVersio().getYlakoodis().size() > 0) {
            Iterator itr = source.getKoodiVersio().getYlakoodis().iterator();
            while(itr.hasNext()) {
                KoodinSuhde koodinSuhde = (KoodinSuhde)itr.next();

                koodinSuhde.getYlakoodiVersio().getMetadatas().size();
                Hibernate.initialize(koodinSuhde.getYlakoodiVersio().getKoodi());
                switch (koodinSuhde.getSuhteenTyyppi()) {
                    case RINNASTEINEN:
                        if (koodinSuhde.getYlakoodiVersio() != null) {
                            converted.getLevelsWithCodeElements().add(koodinSuhde.getYlakoodiVersio().getKoodi().getKoodiUri());
                        }
                        break;
                    case SISALTYY:
                        if (koodinSuhde.getYlakoodiVersio() != null) {
                            converted.getWithinCodeElements().add(koodinSuhde.getYlakoodiVersio().getKoodi().getKoodiUri());
                        }
                        break;
                }
            }
        }
        if (source.getKoodiVersio().getAlakoodis() != null && source.getKoodiVersio().getAlakoodis().size() > 0) {
            Iterator itr = source.getKoodiVersio().getAlakoodis().iterator();
            while(itr.hasNext()) {
                KoodinSuhde koodinSuhde = (KoodinSuhde)itr.next();

                koodinSuhde.getAlakoodiVersio().getMetadatas().size();
                Hibernate.initialize(koodinSuhde.getAlakoodiVersio().getKoodi());
                switch (koodinSuhde.getSuhteenTyyppi()) {
                    case RINNASTEINEN:
                        if (koodinSuhde.getAlakoodiVersio() != null) {
                            converted.getLevelsWithCodeElements().add(koodinSuhde.getAlakoodiVersio().getKoodi().getKoodiUri());
                        }
                        break;
                    case SISALTYY:
                        if (koodinSuhde.getAlakoodiVersio() != null) {
                            converted.getIncludesCodeElements().add(koodinSuhde.getAlakoodiVersio().getKoodi().getKoodiUri());
                        }
                        break;
                }
            }
        }


        converted.getMetadata().addAll(source.getKoodiVersio().getMetadatas());
        converted.setPaivitysPvm(source.getKoodiVersio().getPaivitysPvm());
        converted.setTila(source.getKoodiVersio().getTila());
        converted.setVersio(source.getKoodiVersio().getVersio());
        converted.setVersion(source.getKoodiVersio().getVersion());
        converted.setVoimassaAlkuPvm(source.getKoodiVersio().getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(source.getKoodiVersio().getVoimassaLoppuPvm());

        if (source.getKoodistoItem() != null) {
            KoodistoItemDto item = new KoodistoItemDto();
            item.setKoodistoUri(source.getKoodistoItem().getKoodistoUri());
            item.setOrganisaatioOid(source.getKoodistoItem().getOrganisaatioOid());

            if (source.getKoodistoItem().getVersios() != null && !source.getKoodistoItem().getVersios().isEmpty()) {
                item.getKoodistoVersios().addAll(source.getKoodistoItem().getVersios());
            }
            converted.setKoodisto(item);
        }

        if (StringUtils.isNotBlank(converted.getKoodiUri()) && converted.getKoodisto() != null
                && StringUtils.isNotBlank(converted.getKoodisto().getKoodistoUri())) {
            converted.setResourceUri(koodistoConfiguration.getKoodiResourceUri(
                    converted.getKoodisto().getKoodistoUri(), converted.getKoodiUri()));
        }

        return converted;
    }
}
