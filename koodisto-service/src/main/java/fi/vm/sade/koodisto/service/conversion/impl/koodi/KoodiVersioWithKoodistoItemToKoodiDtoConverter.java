package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.properties.OphProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;


public class KoodiVersioWithKoodistoItemToKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, KoodiDto> {

    private OphProperties ophProperties;

    public KoodiVersioWithKoodistoItemToKoodiDtoConverter(OphProperties ophProperties) {
        this.ophProperties = ophProperties;
    }

    @Override
    public KoodiDto convert(KoodiVersioWithKoodistoItem source) {
        KoodiDto converted = new KoodiDto();

        converted.setKoodiArvo(source.getKoodiVersio().getKoodiarvo());
        converted.setKoodiUri(source.getKoodiVersio().getKoodi().getKoodiUri());

        converted.getMetadata().addAll(source.getKoodiVersio().getMetadatas());
        converted.setPaivitysPvm(source.getKoodiVersio().getPaivitysPvm());
        converted.setPaivittajaOid(source.getKoodiVersio().getPaivittajaOid());
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

        if (!Strings.isNullOrEmpty(converted.getKoodiUri()) && converted.getKoodisto() != null
                && StringUtils.hasLength(converted.getKoodisto().getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodiUriFormat"), converted.getKoodiUri());
            converted.setResourceUri(resourceUri);
        }

        return converted;
    }
}
