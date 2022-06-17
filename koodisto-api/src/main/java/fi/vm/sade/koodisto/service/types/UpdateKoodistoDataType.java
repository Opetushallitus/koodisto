
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.TilaType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UpdateKoodistoDataType extends CreateKoodistoDataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "error.codesGroupUri.blank")
    protected String codesGroupUri;
    @NotBlank(message = "error.koodistoUri.blank")
    protected String koodistoUri;
    @NotNull(message = "error.tila.empty")
    protected TilaType tila;
    @Min(value = 1, message = "error.versio.less.than.one")
    protected int versio;
    @Min(value = 0, message = "error.lockingVersion.less.than.zero")
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
