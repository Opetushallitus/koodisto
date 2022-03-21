
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;

import java.io.Serializable;

public class UpdateKoodistoDataType extends CreateKoodistoDataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String codesGroupUri;
    protected String koodistoUri;
    protected TilaType tila;
    protected int versio;
    protected long lockingVersion;
    public String getCodesGroupUri() {
        return codesGroupUri;
    }

    public void setCodesGroupUri(String value) {
        this.codesGroupUri = value;
    }

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public TilaType getTila() {
        return tila;
    }

    public void setTila(TilaType value) {
        this.tila = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
    }

    public long getLockingVersion() {
        return lockingVersion;
    }
    public void setLockingVersion(long value) {
        this.lockingVersion = value;
    }

}
