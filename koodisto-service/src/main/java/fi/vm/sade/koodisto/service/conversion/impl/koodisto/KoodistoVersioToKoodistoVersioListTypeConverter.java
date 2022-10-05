package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.springframework.stereotype.Component;

@Component("koodistoVersioToKoodistoVersioListTypeConverter")
public class KoodistoVersioToKoodistoVersioListTypeConverter implements
        AbstractFromDomainConverter<KoodistoVersio, KoodistoVersioListType> {

    @Override
    public KoodistoVersioListType convert(KoodistoVersio source) {
        KoodistoVersioListType converted = new KoodistoVersioListType();
        converted.setKoodistoUri(source.getKoodisto().getKoodistoUri());
        converted.setPaivitysPvm(source.getPaivitysPvm() != null ? source.getPaivitysPvm()
                : null);
        converted.setTila(TilaType.valueOf(source.getTila().name()));
        converted.setVersio(source.getVersio());
        converted.setVoimassaAlkuPvm(source.getVoimassaAlkuPvm() != null ? source
                .getVoimassaAlkuPvm() : null);
        converted.setVoimassaLoppuPvm(source.getVoimassaLoppuPvm() != null ? source
                .getVoimassaLoppuPvm() : null);

        KoodistoMetadataToKoodistoMetadataTypeConverter metaConverter = new KoodistoMetadataToKoodistoMetadataTypeConverter();
        for (KoodistoMetadata meta : source.getMetadatas()) {
            converted.getMetadataList().add(metaConverter.convert(meta));
        }

        return converted;
    }

}
