package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.DateHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("koodistoVersioToKoodistoTypeConverter")
public class KoodistoVersioToKoodistoTypeConverter extends AbstractFromDomainConverter<KoodistoVersio, KoodistoType> {

    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    @Override
    public KoodistoType convert(KoodistoVersio source) {

        KoodistoType converted = new KoodistoType();
        converted.setKoodistoUri(source.getKoodisto().getKoodistoUri());

        if (StringUtils.isNotBlank(converted.getKoodistoUri())) {
            converted.setResourceUri(koodistoConfiguration.getKoodistoResourceUri(converted.getKoodistoUri()));
        }
        converted.setOmistaja(source.getKoodisto().getOmistaja());
        converted.setOrganisaatioOid(source.getKoodisto().getOrganisaatioOid());
        converted.setLukittu(source.getKoodisto().getLukittu());

        if (source.getPaivitysPvm() != null) {
            converted.setPaivitysPvm(DateHelper.DateToXmlCal(source.getPaivitysPvm()));
        }
        Optional.ofNullable(source.getPaivittajaOid()).ifPresent(converted::setPaivittajaOid);
        converted.setTila(TilaType.valueOf(source.getTila().name()));
        converted.setVersio(source.getVersio());

        if (source.getVoimassaAlkuPvm() != null) {
            converted.setVoimassaAlkuPvm(DateHelper.DateToXmlCal(source.getVoimassaAlkuPvm()));
        }

        if (source.getVoimassaLoppuPvm() != null) {
            converted.setVoimassaLoppuPvm(DateHelper.DateToXmlCal(source.getVoimassaLoppuPvm()));
        }

        KoodistoMetadataToKoodistoMetadataTypeConverter mdConv = new KoodistoMetadataToKoodistoMetadataTypeConverter();
        for (KoodistoMetadata md : source.getMetadatas()) {
            converted.getMetadataList().add(mdConv.convert(md));
        }

        converted.setLockingVersion(source.getVersion());

        return converted;
    }
}
