package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.Tila;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.40
 */
public class KoodiDto {

    private String koodiUri;

    private String resourceUri;

    private Long version;

    private int versio;

    private KoodistoItemDto koodisto;

    private String koodiArvo;

    protected Date paivitysPvm;

    protected String paivittajaOid;

    protected Date voimassaAlkuPvm;

    protected Date voimassaLoppuPvm;

    protected Tila tila;

    protected List<KoodiMetadata> metadata = new ArrayList<KoodiMetadata>();

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int versio) {
        this.versio = versio;
    }

    public KoodistoItemDto getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(KoodistoItemDto koodisto) {
        this.koodisto = koodisto;
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String koodiArvo) {
        this.koodiArvo = koodiArvo;
    }

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = paivitysPvm;
    }

    public String getPaivittajaOid() {
        return paivittajaOid;
    }

    public void setPaivittajaOid(String paivittajaOid) {
        this.paivittajaOid = paivittajaOid;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date voimassaAlkuPvm) {
        this.voimassaAlkuPvm = voimassaAlkuPvm;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date voimassaLoppuPvm) {
        this.voimassaLoppuPvm = voimassaLoppuPvm;
    }

    public Tila getTila() {
        return tila;
    }

    public void setTila(Tila tila) {
        this.tila = tila;
    }

    public List<KoodiMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<KoodiMetadata> metadata) {
        this.metadata = metadata;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}
