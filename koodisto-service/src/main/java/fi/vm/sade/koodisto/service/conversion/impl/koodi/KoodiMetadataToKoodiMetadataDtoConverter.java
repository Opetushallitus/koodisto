package fi.vm.sade.koodisto.service.conversion.impl.koodi;

import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import org.springframework.core.convert.converter.Converter;

public class KoodiMetadataToKoodiMetadataDtoConverter implements
        Converter<KoodiMetadata, KoodiMetadataDto> {

    @Override
    public KoodiMetadataDto convert(KoodiMetadata metadata) {
        return KoodiMetadataDto.builder()
                .nimi(metadata.getNimi())
                .kuvaus(metadata.getKuvaus())
                .lyhytNimi(metadata.getLyhytNimi())
                .kayttoohje(metadata.getKayttoohje())
                .kasite(metadata.getKasite())
                .sisaltaaMerkityksen(metadata.getSisaltaaMerkityksen())
                .eiSisallaMerkitysta(metadata.getEiSisallaMerkitysta())
                .huomioitavaKoodi(metadata.getHuomioitavaKoodi())
                .sisaltaaKoodiston(metadata.getSisaltaaKoodiston())
                .kieli(metadata.getKieli())
                .koodiVersio(metadata.getKoodiVersio())
                .build();
    }
}
