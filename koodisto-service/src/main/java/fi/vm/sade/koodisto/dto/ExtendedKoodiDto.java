package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.Tila;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.40
 */
public class ExtendedKoodiDto {

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private String koodiUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String resourceUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private int versio;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private KoodistoItemDto koodisto;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    private String koodiArvo;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date paivitysPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date voimassaAlkuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Date voimassaLoppuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    protected Tila tila;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    protected List<KoodiMetadata> metadata = new ArrayList<KoodiMetadata>();

    @JsonView({JsonViews.Extended.class})
    protected List<String> withinCodeElements = new ArrayList<String>();

    @JsonView({JsonViews.Extended.class})
    protected List<String> includesCodeElements = new ArrayList<String>();

    @JsonView({JsonViews.Extended.class})
    protected List<String> levelsWithCodeElements = new ArrayList<String>();


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

    public List<String> getWithinCodeElements() {
        return withinCodeElements;
    }

    public void setWithinCodeElements(final List<String> withinCodeElements) {
        this.withinCodeElements = withinCodeElements;
    }

    public List<String> getIncludesCodeElements() {
        return includesCodeElements;
    }

    public void setIncludesCodeElements(final List<String> includesCodeElements) {
        this.includesCodeElements = includesCodeElements;
    }

    public List<String> getLevelsWithCodeElements() {
        return levelsWithCodeElements;
    }

    public void setLevelsWithCodeElements(final List<String> levelsWithCodeElements) {
        this.levelsWithCodeElements = levelsWithCodeElements;
    }
}
