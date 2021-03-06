package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.05
 */
public abstract class AbstractKoodistoDto {

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Simple.class})
    private String koodistoUri;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Basic.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class})
    private String omistaja;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class,JsonViews.Simple.class})
    private String organisaatioOid;

    @JsonView({JsonViews.Extended.class,JsonViews.Basic.class})
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
