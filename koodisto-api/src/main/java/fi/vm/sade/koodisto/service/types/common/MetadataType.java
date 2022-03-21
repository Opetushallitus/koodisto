package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;

public class MetadataType implements Serializable {

    protected KieliType kieli;

    protected String nimi;
    protected String kuvaus;

    public KieliType getKieli() {
        return kieli;
    }

    public void setKieli(KieliType value) {
        this.kieli = value;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String value) {
        this.nimi = value;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String value) {
        this.kuvaus = value;
    }

}
