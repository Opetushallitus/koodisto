package fi.vm.sade.koodisto.service.types.common;

import java.util.Date;

public class KoodiAndKoodistoSharedType {

    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected String paivittajaOid;
    protected TilaType tila;

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = paivitysPvm;
    }

    protected Date paivitysPvm;

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

}
