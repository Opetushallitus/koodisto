package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.18
 */
public class KoodistoDto extends AbstractKoodistoDto {

    @JsonView(JsonViews.Basic.class)
    private int versio;

    @JsonView(JsonViews.Basic.class)
    private Date paivitysPvm;

    @JsonView(JsonViews.Basic.class)
    private Date voimassaAlkuPvm;

    @JsonView(JsonViews.Basic.class)
    private Date voimassaLoppuPvm;

    @JsonView(JsonViews.Basic.class)
    private Tila tila;

    @JsonView(JsonViews.Basic.class)
    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

    public int getVersio() {
        return versio;
    }

    public void setVersio(int versio) {
        this.versio = versio;
    }

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = paivitysPvm;
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

    public List<KoodistoMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<KoodistoMetadata> metadata) {
        this.metadata = metadata;
    }
}
