
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoodiType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodiUri;
    protected String resourceUri;
    protected KoodistoItemType koodisto;
    protected int versio;
    protected String koodiArvo;
    protected Date paivitysPvm; // TODO xml > DATE OK?
    protected String paivittajaOid;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected TilaType tila;
    protected List<KoodiMetadataType> metadata;
    protected long lockingVersion;
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

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date value) {
        this.paivitysPvm = value;
    }

    public String getPaivittajaOid() {
        return paivittajaOid;
    }

    public void setPaivittajaOid(String value) {
        this.paivittajaOid = value;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date value) {
        this.voimassaAlkuPvm = value;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date value) {
        this.voimassaLoppuPvm = value;
    }

    public TilaType getTila() {
        return tila;
    }

    public void setTila(TilaType value) {
        this.tila = value;
    }

    public List<KoodiMetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<KoodiMetadataType>();
        }
        return this.metadata;
    }

    public long getLockingVersion() {
        return lockingVersion;
    }

    public void setLockingVersion(long value) {
        this.lockingVersion = value;
    }
}
