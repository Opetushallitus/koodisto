package fi.vm.sade.koodisto.service.impl.conversion.koodistoryhma;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaMetadataType;
import org.springframework.stereotype.Component;

@Component("koodistoRyhmaMetadataToKoodistoRyhmaMetadataTypeConverter")
public class KoodistoRyhmaMetadataToKoodistoRyhmaMetadataTypeConverter extends AbstractFromDomainConverter<KoodistoRyhmaMetadata, KoodistoRyhmaMetadataType> {

    @Override
    public KoodistoRyhmaMetadataType convert(KoodistoRyhmaMetadata source) {
        KoodistoRyhmaMetadataType converted = new KoodistoRyhmaMetadataType();
        converted.setKieli(KieliType.valueOf(source.getKieli().name()));
        converted.setNimi(source.getNimi());
        return converted;
    }

}
