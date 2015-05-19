package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.impl.conversion.MetadataToSimpleMetadataConverter;

@Component("koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter")
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, ExtendedKoodiDto> {

    @Autowired
    private KoodiVersioDAO koodiVersioDAO;

    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    @Override
    public ExtendedKoodiDto convert(KoodiVersioWithKoodistoItem source) {
        ExtendedKoodiDto converted = new ExtendedKoodiDto();

        KoodiVersio sourceKoodiVersio = source.getKoodiVersio();
        converted.setKoodiArvo(sourceKoodiVersio.getKoodiarvo());
        converted.setKoodiUri(sourceKoodiVersio.getKoodi().getKoodiUri());

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getYlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getYlakoodiVersio();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                converted.getLevelsWithCodeElements().add(makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            case SISALTYY:
                converted.getWithinCodeElements().add(makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            }
        }

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getAlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getAlakoodiVersio();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                converted.getLevelsWithCodeElements().add(makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            case SISALTYY:
                converted.getIncludesCodeElements().add(makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
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

    private RelationCodeElement makeRelationCodeElement(KoodiVersio koodiVersio, boolean passive) {
        final String koodiUri = koodiVersio.getKoodi().getKoodiUri();
        final Integer versio = koodiVersio.getVersio();
        final String koodiArvo = koodiVersio.getKoodiarvo();
        List<SimpleMetadataDto> metadatas = new ArrayList<SimpleMetadataDto>(Collections2.transform(koodiVersio.getMetadatas(),
                new Function<KoodiMetadata, SimpleMetadataDto>() {

                    @Override
                    public SimpleMetadataDto apply(KoodiMetadata input) {
                        return MetadataToSimpleMetadataConverter.convert(input);
                    }

                }));
        return new RelationCodeElement(koodiUri, versio, koodiArvo, metadatas, getKoodistoMetadatas(koodiVersio), passive);
    }

    private List<SimpleMetadataDto> getKoodistoMetadatas(KoodiVersio kv) {
        KoodistoVersio koodistoVersio = getMatchingKoodistoVersio(kv);
        if (koodistoVersio!=null) {
            return new ArrayList<SimpleMetadataDto>(Collections2.transform(koodistoVersio.getMetadatas(), new Function<KoodistoMetadata, SimpleMetadataDto>() {

                @Override
                public SimpleMetadataDto apply(KoodistoMetadata input) {
                    return MetadataToSimpleMetadataConverter.convert(input);
                }
            }));
        } else {
            return new ArrayList<SimpleMetadataDto>();
        }
    }

    private KoodistoVersio getMatchingKoodistoVersio(KoodiVersio kv) {
        for (KoodistoVersioKoodiVersio kvkv : kv.getKoodistoVersios()) {
            if(kvkv.getKoodiVersio().getVersio().equals(kv.getVersio())) {
                return kvkv.getKoodistoVersio();
            }
        }
        return null;
    }

}
