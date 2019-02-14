package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoItemDto;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component("koodiVersioWithKoodistoItemToKoodiDtoConverter")
public class KoodiVersioWithKoodistoItemToKoodiDtoConverter implements
        Converter<KoodiVersioWithKoodistoItem, KoodiDto> {

    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    public void setKoodistoConfiguration(HostAwareKoodistoConfiguration koodistoConfiguration) {
        this.koodistoConfiguration = koodistoConfiguration;
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

        if (StringUtils.isNotBlank(converted.getKoodiUri()) && converted.getKoodisto() != null
                && StringUtils.isNotBlank(converted.getKoodisto().getKoodistoUri())) {
            converted.setResourceUri(koodistoConfiguration.getKoodiResourceUri(
                    converted.getKoodisto().getKoodistoUri(), converted.getKoodiUri()));
        }

        return converted;
    }
}
