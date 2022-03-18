package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.Kieli;

// TODO tsekkaa toiminta @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SimpleKoodiMetadataDto extends SimpleMetadataDto {
    
    public final String lyhytNimi;
    
    public SimpleKoodiMetadataDto(String nimi, Kieli kieli, String kuvaus, String lyhytNimi) {
        super(nimi, kieli, kuvaus);
        this.lyhytNimi = lyhytNimi;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((lyhytNimi == null) ? 0 : lyhytNimi.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleKoodiMetadataDto other = (SimpleKoodiMetadataDto) obj;
        if (lyhytNimi == null) {
            if (other.lyhytNimi != null)
                return false;
        } else if (!lyhytNimi.equals(other.lyhytNimi))
            return false;
        return true;
    }
    
    

}
