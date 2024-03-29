package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.springframework.stereotype.Component;

@Component
public class KoodistoMetadataToKoodistoMetadataTypeConverter implements
        AbstractFromDomainConverter<KoodistoMetadata, KoodistoMetadataType> {

    @Override
    public KoodistoMetadataType convert(KoodistoMetadata meta) {
        KoodistoMetadataType dto = new KoodistoMetadataType();
        dto.setHuomioitavaKoodisto(meta.getHuomioitavaKoodisto());
        dto.setKasite(meta.getKasite());
        dto.setKayttoohje(meta.getKayttoohje());
        dto.setKieli(KieliType.valueOf(meta.getKieli().name()));
        dto.setKohdealue(meta.getKohdealue());
        dto.setKohdealueenOsaAlue(meta.getKohdealueenOsaAlue());
        dto.setKoodistonLahde(meta.getKoodistonLahde());
        dto.setKuvaus(meta.getKuvaus());
        dto.setNimi(meta.getNimi());
        dto.setSitovuustaso(meta.getSitovuustaso());
        dto.setTarkentaaKoodistoa(meta.getTarkentaaKoodistoa());
        dto.setToimintaymparisto(meta.getToimintaymparisto());
        return dto;
    }
}
