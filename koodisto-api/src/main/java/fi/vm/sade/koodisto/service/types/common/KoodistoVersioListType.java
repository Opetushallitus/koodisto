
package fi.vm.sade.koodisto.service.types.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoodistoVersioListType implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String koodistoUri;
    protected int versio;
    protected Date paivitysPvm;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected TilaType tila;
    private List<KoodistoMetadataType> metadataList;
    protected KoodistoListType koodisto;

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public void setKoodistoUri(String value) {
        this.koodistoUri = value;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int value) {
        this.versio = value;
    }

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date value) {
        this.paivitysPvm = value;
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

    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<KoodistoMetadataType>();
        }
        return this.metadataList;
    }

    public KoodistoListType getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(KoodistoListType value) {
        this.koodisto = value;
    }

}
