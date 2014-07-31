package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
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
    private KoodiVersioDAO koodiVersioDAO;

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

    @Override
    public ExtendedKoodiDto convert(KoodiVersioWithKoodistoItem source) {
        ExtendedKoodiDto converted = new ExtendedKoodiDto();

        KoodiVersio sourceKoodiVersio = source.getKoodiVersio();
        final boolean isLatest = koodiVersioDAO.isLatestKoodiVersio(sourceKoodiVersio.getKoodi().getKoodiUri(), sourceKoodiVersio.getVersio());
        converted.setKoodiArvo(sourceKoodiVersio.getKoodiarvo());
        converted.setKoodiUri(sourceKoodiVersio.getKoodi().getKoodiUri());

        List<String> withinCodeElements = new ArrayList<String>();
        List<String> includesCodeElements = new ArrayList<String>();
        List<String> levelsWithCodeElements = new ArrayList<String>();

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getYlakoodis()) {
            String koodiUri = koodinSuhde.getYlakoodiVersio().getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                levelsWithCodeElements.add(koodiUri);
                break;
            case SISALTYY:
                withinCodeElements.add(koodiUri);
                break;
            }
        }
        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getAlakoodis()) {
            String koodiUri = koodinSuhde.getAlakoodiVersio().getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                levelsWithCodeElements.add(koodiUri);
                break;
            case SISALTYY:
                includesCodeElements.add(koodiUri);
                break;
            }
        }

        Map<String, Integer> includesMap = koodiVersioDAO.getLatestVersionNumbersForUris(includesCodeElements.toArray(new String[includesCodeElements.size()]));
        Map<String, Integer> withinMap = koodiVersioDAO.getLatestVersionNumbersForUris(withinCodeElements.toArray(new String[withinCodeElements.size()]));
        Map<String, Integer> levelsWithMap = koodiVersioDAO.getLatestVersionNumbersForUris(levelsWithCodeElements.toArray(new String[levelsWithCodeElements.size()]));

        //
        HashSet<String> includesAddedUris = new HashSet<String>();
        HashSet<String> withinAddedUris = new HashSet<String>();
        HashSet<String> levelsWithAddedUris = new HashSet<String>();

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getYlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getYlakoodiVersio();
            String koodiUri = koodiVersio.getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                addOrUpdate(converted.getLevelsWithCodeElements(), levelsWithMap, koodiUri, koodiVersio, isLatest, levelsWithAddedUris);
                break;
            case SISALTYY:
                addOrUpdate(converted.getWithinCodeElements(), withinMap, koodiUri, koodiVersio, isLatest, includesAddedUris);
                break;
            }
        }

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getAlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getAlakoodiVersio();
            String koodiUri = koodiVersio.getKoodi().getKoodiUri();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                addOrUpdate(converted.getLevelsWithCodeElements(), levelsWithMap, koodiUri, koodiVersio, isLatest, levelsWithAddedUris);
                break;
            case SISALTYY:
                addOrUpdate(converted.getIncludesCodeElements(), includesMap, koodiUri, koodiVersio, isLatest, withinAddedUris);
                break;
            }
        }

        converted.getMetadata().addAll(sourceKoodiVersio.getMetadatas());
        converted.setPaivitysPvm(sourceKoodiVersio.getPaivitysPvm());
        converted.setTila(sourceKoodiVersio.getTila());
        converted.setVersio(sourceKoodiVersio.getVersio());
        converted.setVersion(sourceKoodiVersio.getVersion());
        converted.setVoimassaAlkuPvm(sourceKoodiVersio.getVoimassaAlkuPvm());
        converted.setVoimassaLoppuPvm(sourceKoodiVersio.getVoimassaLoppuPvm());

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
     * @param latestCodeVersionsMap
     * @param koodiUri
     * @param versio
     */
    private void addOrUpdate(List<RelationCodeElement> list, Map<String, Integer> latestCodeVersionsMap, String koodiUri, KoodiVersio koodiVersio,
            boolean isSourceLatest, HashSet<String> addedUris) {
        final Integer versio = koodiVersio.getVersio();
        final String koodiArvo = koodiVersio.getKoodiarvo();
        if (isSourceLatest && latestCodeVersionsMap.get(koodiVersio.getKoodi().getKoodiUri()).intValue() != versio) {
            return;
        }
        List<SimpleMetadataDto> metadatas = new ArrayList<SimpleMetadataDto>(Collections2.transform(koodiVersio.getMetadatas(),
                new Function<KoodiMetadata, SimpleMetadataDto>() {

                    @Override
                    public SimpleMetadataDto apply(KoodiMetadata input) {
                        return MetadataToSimpleMetadataConverter.convert(input);
                    }

                }));
        if (addedUris.contains(koodiUri)) {
            for (int i = 0; i < list.size(); i++) {
                RelationCodeElement relationCodeElement = list.get(i);
                // Jos koodien versiot ovat listassa väärässä versiojärjestyksessä (uudempi tulee myöhemmin)
                if (versio > relationCodeElement.codeElementVersion) {
                    list.set(i, new RelationCodeElement(koodiUri, versio, koodiArvo, metadatas, getKoodistoMetadatas(koodiVersio)));
                }
            }
        } else {
            addedUris.add(koodiUri);
            list.add(new RelationCodeElement(koodiUri, versio, koodiArvo, metadatas, getKoodistoMetadatas(koodiVersio)));
        }
    }

    private List<SimpleMetadataDto> getKoodistoMetadatas(KoodiVersio kv) {
        KoodistoVersio latest = null;
        for (KoodistoVersioKoodiVersio kvkv : kv.getKoodistoVersios()) {
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
