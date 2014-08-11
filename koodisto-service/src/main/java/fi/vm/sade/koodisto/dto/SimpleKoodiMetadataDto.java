package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;

public class SimpleKoodiMetadataDto extends SimpleMetadataDto {
    
    public final String lyhytNimi;
    
    public SimpleKoodiMetadataDto(String nimi, Kieli kieli, String kuvaus, String lyhytNimi) {
        super(nimi, kieli, kuvaus);
        this.lyhytNimi = lyhytNimi;
    }

}
