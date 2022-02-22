
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodistoItemType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodistoUri;
    protected String organisaatioOid;
    protected List<Integer> koodistoVersio;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String value) {
        this.organisaatioOid = value;
    }

    public List<Integer> getKoodistoVersio() {
        if (koodistoVersio == null) {
            koodistoVersio = new ArrayList<Integer>();
        }
        return this.koodistoVersio;
    }
}
