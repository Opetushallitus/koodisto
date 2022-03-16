package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

public class KoodiVersioWithKoodistoVersioItemsToKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, KoodiDto> {

    private OphProperties ophProperties;

    public KoodiVersioWithKoodistoVersioItemsToKoodiDtoConverter(OphProperties ophProperties) {
        this.ophProperties = ophProperties;
    }

    public KoodiDto convert(KoodiVersioWithKoodistoItem source) {
        KoodiDto converted = new KoodiDto();

        converted.setKoodiArvo(source.getKoodiVersio().getKoodiarvo());
        converted.setKoodiUri(source.getKoodiVersio().getKoodi().getKoodiUri());
        converted.getMetadata().addAll(source.getKoodiVersio().getMetadatas());
        converted.setPaivitysPvm(source.getKoodiVersio().getPaivitysPvm());
        converted.setPaivittajaOid(source.getKoodiVersio().getPaivittajaOid());
        converted.setTila(source.getKoodiVersio().getTila());
        converted.setVersio(source.getKoodiVersio().getVersio());
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

        if (!converted.getKoodiUri().isBlank() && converted.getKoodisto() != null
                && !converted.getKoodisto().getKoodistoUri().isBlank()) {
            // TODO tsekkaa
            String resourceUri = MessageFormat.format(ophProperties.url("koodiUri"), converted.getKoodiUri(), converted.getKoodisto().getKoodistoUri());
            converted.setResourceUri(resourceUri);
        }

        return converted;
    }
}
