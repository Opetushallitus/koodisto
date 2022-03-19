
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodistoListType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodistoUri;
    protected String omistaja;
    protected String organisaatioOid;
    protected Boolean lukittu;
    protected KoodistoVersioListType latestKoodistoVersio;
    private List<KoodistoVersioListType> koodistoVersios;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public String getOmistaja() {
        return omistaja;
    }

    public void setOmistaja(String value) {
        this.omistaja = value;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String value) {
        this.organisaatioOid = value;
    }

    public Boolean isLukittu() {
        return lukittu;
    }

    public void setLukittu(Boolean value) {
        this.lukittu = value;
    }

    public KoodistoVersioListType getLatestKoodistoVersio() {
        return latestKoodistoVersio;
    }

    public void setLatestKoodistoVersio(KoodistoVersioListType value) {
        this.latestKoodistoVersio = value;
    }

    public List<KoodistoVersioListType> getKoodistoVersios() {
        if (koodistoVersios == null) {
            koodistoVersios = new ArrayList<KoodistoVersioListType>();
        }
        return this.koodistoVersios;
    }

}
