
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateKoodiDataType extends CreateKoodiDataType implements Serializable
{

    private static final long serialVersionUID = 1L;

    protected String koodiUri;

    protected UpdateKoodiTilaType tila;

    protected int versio;
    protected long lockingVersion;

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String value) {
        this.koodiUri = value;
    }

    public UpdateKoodiTilaType getTila() {
        return tila;
    }

    public void setTila(UpdateKoodiTilaType value) {
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
