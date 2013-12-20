package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("koodistoVersioToKoodistoTypeConverter")
public class KoodistoVersioToKoodistoTypeConverter extends AbstractFromDomainConverter<KoodistoVersio, KoodistoType> {

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

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
