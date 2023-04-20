package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;

@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public class SimpleKoodiMetadataDto extends SimpleMetadataDto {
    
    @JsonView(JsonViews.Basic.class)
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
            return other.lyhytNimi == null;
        } else return lyhytNimi.equals(other.lyhytNimi);
    }
    
    

}
