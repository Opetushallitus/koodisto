package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.impl.conversion.MetadataToSimpleMetadataConverter;


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
        for(KoodinSuhde koodinSuhde : source.getKoodiVersio().getYlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getYlakoodiVersio();
            String koodiUri = koodiVersio.getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                addOrUpdate(converted.getLevelsWithCodeElements(), koodiUri, koodiVersio);
                break;
            case SISALTYY:
                addOrUpdate(converted.getWithinCodeElements(), koodiUri, koodiVersio);
                break;
            }
        }
        for(KoodinSuhde koodinSuhde : source.getKoodiVersio().getAlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getAlakoodiVersio();
            String koodiUri = koodiVersio.getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                addOrUpdate(converted.getLevelsWithCodeElements(), koodiUri, koodiVersio);
                break;
            case SISALTYY:
                addOrUpdate(converted.getIncludesCodeElements(), koodiUri, koodiVersio);
                break;
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

    /**
     * Lisää koodin listaan, jos listalla on jo kyseinen koodi vanhemmalla versiolla, päivittää sen
     * 
     * @param list
     * @param koodiUri
     * @param versio
     */
    private void addOrUpdate(List<RelationCodeElement> list, String koodiUri, KoodiVersio koodiVersio) {
        Integer versio = koodiVersio.getVersio();
        List<SimpleMetadataDto> metadatas = new ArrayList<SimpleMetadataDto>(Collections2.transform(koodiVersio.getMetadatas(), new Function<KoodiMetadata, SimpleMetadataDto>() {

            @Override
            public SimpleMetadataDto apply(KoodiMetadata input) {
                return MetadataToSimpleMetadataConverter.convert(input);
            }
            
        }));
        boolean duplicate = false;
        for (int i = 0; i < list.size(); i++) {
            RelationCodeElement relationCodeElement = list.get(i);
            if (relationCodeElement.codeElementUri.equals(koodiUri)) {
                duplicate = true;
                // Jos koodien versiot ovat listassa väärässä versiojärjestyksessä (uudempi tulee myöhemmin)
                if (versio > relationCodeElement.codeElementVersion) {
                    list.set(i, new RelationCodeElement(koodiUri, versio, metadatas, getKoodistoMetadatas(koodiVersio)));
                }
            }
        }
        if (!duplicate) {
            list.add(new RelationCodeElement(koodiUri, versio, metadatas, getKoodistoMetadatas(koodiVersio)));
        }
    }
    
    private List<SimpleMetadataDto> getKoodistoMetadatas(KoodiVersio kv) {
        KoodistoVersio latest = null;
        for (KoodistoVersioKoodiVersio kvkv: kv.getKoodistoVersios()) {
            KoodistoVersio koodistoVersio = kvkv.getKoodistoVersio();
            latest = (latest == null || koodistoVersio.getVersio() > latest.getVersio()) ? koodistoVersio : latest;     
        }
        return new ArrayList<SimpleMetadataDto>(Collections2.transform(latest.getMetadatas(), new Function<KoodistoMetadata, SimpleMetadataDto>() {

            @Override
            public SimpleMetadataDto apply(KoodistoMetadata input) {
                return MetadataToSimpleMetadataConverter.convert(input);
            }
        }));
    }


}
