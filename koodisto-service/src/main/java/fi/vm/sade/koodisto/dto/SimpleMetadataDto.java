package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Kieli;

import java.util.Date;

public class SimpleMetadataDto {
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final String nimi;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final Kieli kieli;
    
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final String kuvaus;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final Date alkuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    public final Date loppuPvm;

    public SimpleMetadataDto(String nimi, Kieli kieli, String kuvaus, Date alkuPvm, Date loppuPvm) {
        this.nimi = nimi;
        this.kieli = kieli;
        this.kuvaus = kuvaus;
        this.alkuPvm = alkuPvm;
        this.loppuPvm = loppuPvm;
    }
    
    @SuppressWarnings("unused")
    private SimpleMetadataDto() {
        nimi = null;
        kieli = null;
        kuvaus = null;
        this.alkuPvm = null;
        this.loppuPvm = null;
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
            if (other.nimi != null)
                return false;
        } else if (!nimi.equals(other.nimi))
            return false;
        if (this.alkuPvm == null && other.alkuPvm != null) {
            return false;
        }
        else if (this.alkuPvm != null && !this.alkuPvm.equals(other.alkuPvm)) {
            return false;
        }
        if (this.loppuPvm == null && other.loppuPvm != null) {
            return false;
        }
        else if (this.loppuPvm != null && !this.loppuPvm.equals(other.loppuPvm)) {
            return false;
        }
        return true;
    }
    
    
}
