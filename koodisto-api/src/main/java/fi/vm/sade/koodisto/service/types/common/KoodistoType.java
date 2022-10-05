
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodistoType extends KoodiAndKoodistoSharedType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodistoUri;
    protected String resourceUri;
    protected int versio;
    protected String omistaja;
    protected String organisaatioOid;
    protected Boolean lukittu;
    protected long lockingVersion;
    private List<KoodistoMetadataType> metadataList;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String value) {
        this.resourceUri = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
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

    public long getLockingVersion() {
        return lockingVersion;
    }

    public void setLockingVersion(long value) {
        this.lockingVersion = value;
    }

    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<>();
        }
        return this.metadataList;
    }
}
