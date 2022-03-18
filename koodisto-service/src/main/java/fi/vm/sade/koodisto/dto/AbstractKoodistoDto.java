package fi.vm.sade.koodisto.dto;

public abstract class AbstractKoodistoDto {

    private String koodistoUri;

    private String resourceUri;

    private String omistaja;

    private String organisaatioOid;

    private Boolean lukittu;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String koodistoUri) {
        this.koodistoUri = koodistoUri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getOmistaja() {
        return omistaja;
    }

    public void setOmistaja(String omistaja) {
        this.omistaja = omistaja;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }

    public Boolean getLukittu() {
        return lukittu;
    }

    public void setLukittu(Boolean lukittu) {
        this.lukittu = lukittu;
    }
}
