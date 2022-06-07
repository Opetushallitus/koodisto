package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.properties.OphProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("koodiVersioWithKoodistoVersioItemsToKoodiTypeConverter")
@RequiredArgsConstructor
public class KoodiVersioWithKoodistoVersioItemsToKoodiTypeConverter implements
        Converter<KoodiVersioWithKoodistoItem, KoodiType> {
    private final OphProperties ophProperties;


    public KoodiType convert(KoodiVersioWithKoodistoItem source) {
        KoodiType converted = new KoodiType();

        converted.setKoodiArvo(source.getKoodiVersio().getKoodiarvo());
        converted.setKoodiUri(source.getKoodiVersio().getKoodi().getKoodiUri());

        KoodiMetadataToKoodiMetadataTypeConverter mConv = new KoodiMetadataToKoodiMetadataTypeConverter();

        List<KoodiMetadataType> metas = new ArrayList<KoodiMetadataType>();
        for (KoodiMetadata md : source.getKoodiVersio().getMetadatas()) {
            metas.add(mConv.convert(md));
        }
        converted.getMetadata().addAll(metas);

        if (source.getKoodiVersio().getPaivitysPvm() != null) {
            converted.setPaivitysPvm((source.getKoodiVersio().getPaivitysPvm()));
        }
        Optional.ofNullable(source.getKoodiVersio().getPaivittajaOid()).ifPresent(converted::setPaivittajaOid);
        converted.setTila(TilaType.valueOf(source.getKoodiVersio().getTila().name()));
        converted.setVersio(source.getKoodiVersio().getVersio());

        if (source.getKoodiVersio().getVoimassaAlkuPvm() != null) {
            converted.setVoimassaAlkuPvm((source.getKoodiVersio().getVoimassaAlkuPvm()));
        }

        if (source.getKoodiVersio().getVoimassaLoppuPvm() != null) {
            converted.setVoimassaLoppuPvm((source.getKoodiVersio().getVoimassaLoppuPvm()));
        }

        if (source.getKoodistoItem() != null) {
            KoodistoItemType item = new KoodistoItemType();
            item.setKoodistoUri(source.getKoodistoItem().getKoodistoUri());
            item.setOrganisaatioOid(source.getKoodistoItem().getOrganisaatioOid());
            if (source.getKoodistoItem().getVersios() != null && !source.getKoodistoItem().getVersios().isEmpty()) {
                item.getKoodistoVersio().addAll(source.getKoodistoItem().getVersios());
            }
            converted.setKoodisto(item);
        }
        if (!Strings.isNullOrEmpty(converted.getKoodiUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodistoUriFormat"), converted.getKoodiUri());
            converted.setResourceUri(resourceUri);
        }

        converted.setLockingVersion(source.getKoodiVersio().getVersion());

        return converted;
    }
}
