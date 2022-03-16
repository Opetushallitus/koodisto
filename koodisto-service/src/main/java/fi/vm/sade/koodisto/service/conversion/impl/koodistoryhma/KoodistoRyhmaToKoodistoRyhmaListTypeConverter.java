package fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma;

import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoToKoodistoListTypeConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaToKoodistoRyhmaListTypeConverter")
public class KoodistoRyhmaToKoodistoRyhmaListTypeConverter extends AbstractFromDomainConverter<KoodistoRyhma, KoodistoRyhmaListType> {

    @Override
    public KoodistoRyhmaListType convert(KoodistoRyhma source) {
        KoodistoRyhmaListType converted = new KoodistoRyhmaListType();
        converted.setKoodistoRyhmaUri(source.getKoodistoRyhmaUri());

        KoodistoRyhmaMetadataToKoodistoRyhmaMetadataTypeConverter metaConverter = new KoodistoRyhmaMetadataToKoodistoRyhmaMetadataTypeConverter();

        for (KoodistoRyhmaMetadata meta : source.getKoodistoJoukkoMetadatas()) {
            converted.getKoodistoRyhmaMetadatas().add(metaConverter.convert(meta));
        }

        KoodistoToKoodistoListTypeConverter koodistoConverter = new KoodistoToKoodistoListTypeConverter();
        for (Koodisto k : source.getKoodistos()) {
            converted.getKoodistos().add(koodistoConverter.convert(k));
        }

        return converted;
    }
}
