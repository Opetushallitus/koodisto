package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.ExtendedConverter;
import fi.vm.sade.koodisto.service.conversion.impl.MetadataToSimpleMetadataConverter;
import fi.vm.sade.properties.OphProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter implements
        ExtendedConverter<KoodiVersioWithKoodistoItem, ExtendedKoodiDto> {

    private final OphProperties ophProperties;
    private final KoodiMetadataToKoodiMetadataDtoConverter koodistoVersioToKoodistoVersioListDtoConverter;

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
                converted.getLevelsWithCodeElements().add(this.makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            case SISALTYY:
                converted.getWithinCodeElements().add(this.makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            }
        }

        for (KoodinSuhde koodinSuhde : sourceKoodiVersio.getAlakoodis()) {
            KoodiVersio koodiVersio = koodinSuhde.getAlakoodiVersio();
            switch (koodinSuhde.getSuhteenTyyppi()) {
            case RINNASTEINEN:
                converted.getLevelsWithCodeElements().add(this.makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            case SISALTYY:
                converted.getIncludesCodeElements().add(this.makeRelationCodeElement(koodiVersio, koodinSuhde.isPassive()));
                break;
            }
        }

        converted.getMetadata().addAll(sourceKoodiVersio.getMetadatas().stream()
                .map(koodistoVersioToKoodistoVersioListDtoConverter::convert)
                .collect(Collectors.toList()));
        converted.setPaivitysPvm(sourceKoodiVersio.getPaivitysPvm());
        converted.setPaivittajaOid(sourceKoodiVersio.getPaivittajaOid());
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

        if (!Strings.isNullOrEmpty(converted.getKoodiUri()) && converted.getKoodisto() != null
                && !Strings.isNullOrEmpty(converted.getKoodisto().getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodiUriFormat"), converted.getKoodiUri());
            converted.setResourceUri(resourceUri);
        }

        return converted;
    }


    private RelationCodeElement makeRelationCodeElement(KoodiVersio koodiVersio, boolean passive) {
        final String koodiUri = koodiVersio.getKoodi().getKoodiUri();
        final Integer versio = koodiVersio.getVersio();
        final String koodiArvo = koodiVersio.getKoodiarvo();
        List<SimpleMetadataDto> metadatas = koodiVersio.getMetadatas().stream()
                .map(MetadataToSimpleMetadataConverter::convert)
                .collect(Collectors.toList());
        return new RelationCodeElement(koodiUri, versio, koodiArvo, metadatas, this.getKoodistoMetadatas(koodiVersio), passive);
    }

    private List<SimpleMetadataDto> getKoodistoMetadatas(KoodiVersio kv) {
        return this.getMatchingKoodistoVersio(kv)
                .map(koodistoVersio -> koodistoVersio.getMetadatas().stream()
                        .map(MetadataToSimpleMetadataConverter::convert)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    private Optional<KoodistoVersio> getMatchingKoodistoVersio(KoodiVersio kv) {
        return kv.getKoodistoVersios().stream()
                .filter(koodistoVersioKoodiVersio -> koodistoVersioKoodiVersio.getKoodiVersio().getVersio().equals(kv.getVersio()))
                .map(KoodistoVersioKoodiVersio::getKoodistoVersio)
                .findFirst();
    }

}
