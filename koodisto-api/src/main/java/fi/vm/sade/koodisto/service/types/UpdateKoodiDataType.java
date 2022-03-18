
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateKoodiDataType implements Serializable
{

    private final static long serialVersionUID = 1L;

    protected String koodiUri;

    protected String koodiArvo;

    protected Date voimassaAlkuPvm;

    protected Date voimassaLoppuPvm;

    protected UpdateKoodiTilaType tila;

    protected List<KoodiMetadataType> metadata;
    protected int versio;
    protected long lockingVersion;

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String value) {
        this.koodiUri = value;
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String value) {
        this.koodiArvo = value;
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

    public UpdateKoodiTilaType getTila() {
        return tila;
    }

    public void setTila(UpdateKoodiTilaType value) {
        this.tila = value;
    }

    public List<KoodiMetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<KoodiMetadataType>();
        }
        return this.metadata;
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
