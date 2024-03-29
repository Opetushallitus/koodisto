package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;

public class SimpleMetadataDto {
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    public final String nimi;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    public final Kieli kieli;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class})
    public final String kuvaus;

    public SimpleMetadataDto(String nimi, Kieli kieli, String kuvaus) {
        this.nimi = nimi;
        this.kieli = kieli;
        this.kuvaus = kuvaus;
    }
    
    @SuppressWarnings("unused")
    private SimpleMetadataDto() {
        nimi = null;
        kieli = null;
        kuvaus = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kieli == null) ? 0 : kieli.hashCode());
        result = prime * result + ((kuvaus == null) ? 0 : kuvaus.hashCode());
        result = prime * result + ((nimi == null) ? 0 : nimi.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleMetadataDto other = (SimpleMetadataDto) obj;
        if (kieli != other.kieli)
            return false;
        if (kuvaus == null) {
            if (other.kuvaus != null)
                return false;
        } else if (!kuvaus.equals(other.kuvaus))
            return false;
        if (nimi == null) {
            return other.nimi == null;
        } else return nimi.equals(other.nimi);
    }
    
    
}
