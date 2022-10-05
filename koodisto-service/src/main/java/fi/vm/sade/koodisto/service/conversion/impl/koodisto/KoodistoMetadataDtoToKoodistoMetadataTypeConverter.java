package fi.vm.sade.koodisto.service.conversion.impl.koodisto;

import fi.vm.sade.koodisto.dto.KoodistoMetadataDto;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KoodistoMetadataDtoToKoodistoMetadataTypeConverter implements
        Converter<KoodistoMetadataDto, KoodistoMetadataType> {

    @Override
    public KoodistoMetadataType convert(KoodistoMetadataDto source) {
        KoodistoMetadataType meta = new KoodistoMetadataType();
        meta.setHuomioitavaKoodisto(source.getHuomioitavaKoodisto());
        meta.setKasite(source.getKasite());
        meta.setKayttoohje(source.getKayttoohje());
        meta.setKieli(KieliType.valueOf(source.getKieli().name()));
        meta.setKohdealue(source.getKohdealue());
        meta.setKohdealueenOsaAlue(source.getKohdealueenOsaAlue());
        meta.setKoodistonLahde(source.getKoodistonLahde());
        meta.setKuvaus(source.getKuvaus());
        meta.setNimi(source.getNimi());
        meta.setSitovuustaso(source.getSitovuustaso());
        meta.setTarkentaaKoodistoa(source.getTarkentaaKoodistoa());
        meta.setToimintaymparisto(source.getToimintaymparisto());
        return meta;
    }

}