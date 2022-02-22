
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateKoodistoDataType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String codesGroupUri;
    protected String koodistoUri;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected TilaType tila;
    protected String omistaja;
    protected String organisaatioOid;
    protected Boolean lukittu;
    protected List<KoodistoMetadataType> metadataList;
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

    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<KoodistoMetadataType>();
        }
        return this.metadataList;
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
