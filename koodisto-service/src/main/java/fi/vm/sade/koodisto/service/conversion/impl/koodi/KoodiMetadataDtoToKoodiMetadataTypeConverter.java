package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import org.springframework.core.convert.converter.Converter;

public class KoodiMetadataDtoToKoodiMetadataTypeConverter implements
        Converter<KoodiMetadataDto, KoodiMetadataType> {

    @Override
    public KoodiMetadataType convert(KoodiMetadataDto dto) {

        KoodiMetadataType type = new KoodiMetadataType();
        type.setEiSisallaMerkitysta(dto.getEiSisallaMerkitysta());
        type.setHuomioitavaKoodi(dto.getHuomioitavaKoodi());
        type.setKasite(dto.getKasite());
        type.setKayttoohje(dto.getKayttoohje());
        type.setKieli(KieliType.valueOf(dto.getKieli().name()));
        type.setKuvaus(dto.getKuvaus());
        type.setLyhytNimi(dto.getLyhytNimi());
        type.setNimi(dto.getNimi());
        type.setSisaltaaKoodiston(dto.getSisaltaaKoodiston());
        type.setSisaltaaMerkityksen(dto.getSisaltaaMerkityksen());

        return type;
    }

}
