package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import org.springframework.stereotype.Component;

@Component
public class KoodiMetadataTypeToKoodiMetadataConverter implements
        AbstractToDomainConverter<KoodiMetadataType, KoodiMetadata> {

    @Override
    public KoodiMetadata convert(KoodiMetadataType dto) {
        KoodiMetadata k = new KoodiMetadata();
        k.setEiSisallaMerkitysta(dto.getEiSisallaMerkitysta());
        k.setHuomioitavaKoodi(dto.getHuomioitavaKoodi());
        k.setKasite(dto.getKasite());
        k.setKayttoohje(dto.getKayttoohje());
        k.setKieli(Kieli.valueOf(dto.getKieli().name()));
        k.setKuvaus(dto.getKuvaus());
        k.setLyhytNimi(dto.getLyhytNimi());
        k.setNimi(dto.getNimi());
        k.setSisaltaaKoodiston(dto.getSisaltaaKoodiston());
        k.setSisaltaaMerkityksen(dto.getSisaltaaMerkityksen());
        return k;
    }

}
