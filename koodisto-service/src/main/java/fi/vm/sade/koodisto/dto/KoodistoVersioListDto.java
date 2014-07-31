package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.util.DateCloner;

import org.codehaus.jackson.map.annotate.JsonView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.09
 */
public class KoodistoVersioListDto {

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private int versio;

    @JsonView(JsonViews.Basic.class)
    private Date paivitysPvm;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private Date voimassaAlkuPvm;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private Date voimassaLoppuPvm;

    @JsonView(JsonViews.Basic.class)
    private Tila tila;

    @JsonView(JsonViews.Basic.class)
    private Long version;

    @JsonView({JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

    public int getVersio() {
        return versio;
    }

    public void setVersio(int versio) {
        this.versio = versio;
    }

    public Date getPaivitysPvm() {
        return DateCloner.clone(paivitysPvm);
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = DateCloner.clone(paivitysPvm);
    }

    public Date getVoimassaAlkuPvm() {
        return DateCloner.clone(voimassaAlkuPvm);
    }

    public void setVoimassaAlkuPvm(Date voimassaAlkuPvm) {
        this.voimassaAlkuPvm = DateCloner.clone(voimassaAlkuPvm);
    }

    public Date getVoimassaLoppuPvm() {
        return DateCloner.clone(voimassaLoppuPvm);
    }

    public void setVoimassaLoppuPvm(Date voimassaLoppuPvm) {
        this.voimassaLoppuPvm = DateCloner.clone(voimassaLoppuPvm);
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}
