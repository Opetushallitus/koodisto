package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import org.springframework.core.convert.converter.Converter;


public class KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, SimpleKoodiDto> {


    @Override
    public SimpleKoodiDto convert(KoodiVersioWithKoodistoItem source) {
        SimpleKoodiDto converted = new SimpleKoodiDto();

        converted.setKoodiArvo(source.getKoodiVersio().getKoodiarvo());
        converted.setKoodiUri(source.getKoodiVersio().getKoodi().getKoodiUri());
        converted.getMetadata().addAll(source.getKoodiVersio().getMetadatas());
        converted.setVersio(source.getKoodiVersio().getVersio());

        return converted;
    }
}
