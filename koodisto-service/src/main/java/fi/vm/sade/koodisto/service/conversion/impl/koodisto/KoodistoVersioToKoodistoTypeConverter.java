package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.properties.OphProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;

@Component("koodistoVersioToKoodistoTypeConverter")
public class KoodistoVersioToKoodistoTypeConverter implements AbstractFromDomainConverter<KoodistoVersio, KoodistoType> {

    private final OphProperties ophProperties;

    public KoodistoVersioToKoodistoTypeConverter(OphProperties ophProperties) {
        this.ophProperties = ophProperties;
    }

    @Override
    public KoodistoType convert(KoodistoVersio source) {

        KoodistoType converted = new KoodistoType();
        converted.setKoodistoUri(source.getKoodisto().getKoodistoUri());

        if (!Strings.isNullOrEmpty(converted.getKoodistoUri())) {
            String resourceUri = MessageFormat.format(ophProperties.url("koodistoUriFormat"), converted.getKoodistoUri());
            converted.setResourceUri(resourceUri);
        }
        converted.setOmistaja(source.getKoodisto().getOmistaja());
        converted.setOrganisaatioOid(source.getKoodisto().getOrganisaatioOid());
        converted.setLukittu(source.getKoodisto().getLukittu());

        if (source.getPaivitysPvm() != null) {
            converted.setPaivitysPvm(source.getPaivitysPvm());
        }
        Optional.ofNullable(source.getPaivittajaOid()).ifPresent(converted::setPaivittajaOid);
        converted.setTila(TilaType.valueOf(source.getTila().name()));
        converted.setVersio(source.getVersio());

        if (source.getVoimassaAlkuPvm() != null) {
            converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm());
        }

        if (source.getVoimassaLoppuPvm() != null) {
            converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm());
        }

        KoodistoMetadataToKoodistoMetadataTypeConverter mdConv = new KoodistoMetadataToKoodistoMetadataTypeConverter();
        for (KoodistoMetadata md : source.getMetadatas()) {
            converted.getMetadataList().add(mdConv.convert(md));
        }

        converted.setLockingVersion(source.getVersion());

        return converted;
    }
}
