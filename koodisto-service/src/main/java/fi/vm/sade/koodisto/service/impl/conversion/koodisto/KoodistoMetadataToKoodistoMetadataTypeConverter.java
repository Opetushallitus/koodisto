package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.springframework.stereotype.Component;

@Component("koodistoMetadataToKoodistoMetadataTypeConverter")
public class KoodistoMetadataToKoodistoMetadataTypeConverter extends
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
