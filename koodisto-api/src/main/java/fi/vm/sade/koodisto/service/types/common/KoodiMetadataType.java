
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;

public class KoodiMetadataType implements Serializable
{

    private final static long serialVersionUID = 1L;

    protected KieliType kieli;

    protected String nimi;
    protected String kuvaus;
    protected String lyhytNimi;
    protected String kayttoohje;
    protected String kasite;
    protected String sisaltaaMerkityksen;
    protected String eiSisallaMerkitysta;
    protected String huomioitavaKoodi;
    protected String sisaltaaKoodiston;

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

    public String getLyhytNimi() {
        return lyhytNimi;
    }

    public void setLyhytNimi(String value) {
        this.lyhytNimi = value;
    }

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

    public String getSisaltaaMerkityksen() {
        return sisaltaaMerkityksen;
    }

    public void setSisaltaaMerkityksen(String value) {
        this.sisaltaaMerkityksen = value;
    }

    public String getEiSisallaMerkitysta() {
        return eiSisallaMerkitysta;
    }

    public void setEiSisallaMerkitysta(String value) {
        this.eiSisallaMerkitysta = value;
    }

    public String getHuomioitavaKoodi() {
        return huomioitavaKoodi;
    }

    public void setHuomioitavaKoodi(String value) {
        this.huomioitavaKoodi = value;
    }

    public String getSisaltaaKoodiston() {
        return sisaltaaKoodiston;
    }

    public void setSisaltaaKoodiston(String value) {
        this.sisaltaaKoodiston = value;
    }

}
