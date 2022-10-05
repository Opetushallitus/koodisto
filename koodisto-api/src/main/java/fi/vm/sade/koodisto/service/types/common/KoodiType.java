
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoodiType extends KoodiAndKoodistoSharedType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodiUri;
    protected String resourceUri;
    protected KoodistoItemType koodisto;
    protected int versio;
    protected String koodiArvo;
    protected long lockingVersion;
    private List<KoodiMetadataType> metadata;

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String value) {
        this.koodiUri = value;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String value) {
        this.resourceUri = value;
    }

    public KoodistoItemType getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(KoodistoItemType value) {
        this.koodisto = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String value) {
        this.koodiArvo = value;
    }

    public long getLockingVersion() {
        return lockingVersion;
    }

    public void setLockingVersion(long value) {
        this.lockingVersion = value;
    }

    public List<KoodiMetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }
}
