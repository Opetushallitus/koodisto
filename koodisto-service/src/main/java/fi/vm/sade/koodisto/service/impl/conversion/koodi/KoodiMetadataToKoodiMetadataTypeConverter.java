package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("koodiMetadataToKoodiMetadataTypeConverter")
public class KoodiMetadataToKoodiMetadataTypeConverter extends
        AbstractFromDomainConverter<KoodiMetadata, KoodiMetadataType> {

    @Override
    public KoodiMetadataType convert(KoodiMetadata k) {

        KoodiMetadataType dto = new KoodiMetadataType();
        dto.setEiSisallaMerkitysta(k.getEiSisallaMerkitysta());
        dto.setHuomioitavaKoodi(k.getHuomioitavaKoodi());
        dto.setKasite(k.getKasite());
        dto.setKayttoohje(k.getKayttoohje());
        dto.setKieli(KieliType.valueOf(k.getKieli().name()));
        dto.setKuvaus(k.getKuvaus());
        dto.setLyhytNimi(k.getLyhytNimi());
        dto.setNimi(k.getNimi());
        dto.setSisaltaaKoodiston(k.getSisaltaaKoodiston());
        dto.setSisaltaaMerkityksen(k.getSisaltaaMerkityksen());
        Optional.ofNullable(k.getAlkuPvm()).ifPresent(alkuPvm -> dto.setAlkuPvm(DateHelper.DateToXmlCal(alkuPvm)));
        Optional.ofNullable(k.getLoppuPvm()).ifPresent(loppuPvm -> dto.setLoppuPvm(DateHelper.DateToXmlCal(loppuPvm)));

        return dto;
    }

}
