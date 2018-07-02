package fi.vm.sade.koodisto.service.impl.conversion.koodisto;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.springframework.stereotype.Component;

@Component("koodistoMetadataTypeToKoodistoMetadataConverter")
public class KoodistoMetadataTypeToKoodistoMetadataConverter extends
        AbstractToDomainConverter<KoodistoMetadataType, KoodistoMetadata> {

    @Override
    public KoodistoMetadata convert(KoodistoMetadataType metaType) {
        KoodistoMetadata meta = new KoodistoMetadata();
        meta.setHuomioitavaKoodisto(metaType.getHuomioitavaKoodisto());
        meta.setKasite(metaType.getKasite());
        meta.setKayttoohje(metaType.getKayttoohje());
        meta.setKieli(Kieli.valueOf(metaType.getKieli().name()));
        meta.setKohdealue(metaType.getKohdealue());
        meta.setKohdealueenOsaAlue(metaType.getKohdealueenOsaAlue());
        meta.setKoodistonLahde(metaType.getKoodistonLahde());
        meta.setKuvaus(metaType.getKuvaus());
        meta.setNimi(metaType.getNimi());
        meta.setSitovuustaso(metaType.getSitovuustaso());
        meta.setTarkentaaKoodistoa(metaType.getTarkentaaKoodistoa());
        meta.setToimintaymparisto(metaType.getToimintaymparisto());
        return meta;
    }
}
