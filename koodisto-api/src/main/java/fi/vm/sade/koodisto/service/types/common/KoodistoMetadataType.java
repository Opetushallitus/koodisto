
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;

public class KoodistoMetadataType extends MetadataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String kayttoohje;
    protected String kasite;
    protected String kohdealue;
    protected String kohdealueenOsaAlue;
    protected String sitovuustaso;
    protected String toimintaymparisto;
    protected String tarkentaaKoodistoa;
    protected String huomioitavaKoodisto;
    protected String koodistonLahde;

    public String getKayttoohje() {
        return kayttoohje;
    }

    public void setKayttoohje(String value) {
        this.kayttoohje = value;
    }

    public String getKasite() {
        return kasite;
    }

    public void setKasite(String value) {
        this.kasite = value;
    }

    public String getKohdealue() {
        return kohdealue;
    }

    public void setKohdealue(String value) {
        this.kohdealue = value;
    }

    public String getKohdealueenOsaAlue() {
        return kohdealueenOsaAlue;
    }

    public void setKohdealueenOsaAlue(String value) {
        this.kohdealueenOsaAlue = value;
    }

    public String getSitovuustaso() {
        return sitovuustaso;
    }

    public void setSitovuustaso(String value) {
        this.sitovuustaso = value;
    }

    public String getToimintaymparisto() {
        return toimintaymparisto;
    }

    public void setToimintaymparisto(String value) {
        this.toimintaymparisto = value;
    }

    public String getTarkentaaKoodistoa() {
        return tarkentaaKoodistoa;
    }

    public void setTarkentaaKoodistoa(String value) {
        this.tarkentaaKoodistoa = value;
    }

    public String getHuomioitavaKoodisto() {
        return huomioitavaKoodisto;
    }

    public void setHuomioitavaKoodisto(String value) {
        this.huomioitavaKoodisto = value;
    }

    public String getKoodistonLahde() {
        return koodistonLahde;
    }

    public void setKoodistonLahde(String value) {
        this.koodistonLahde = value;
    }

}
