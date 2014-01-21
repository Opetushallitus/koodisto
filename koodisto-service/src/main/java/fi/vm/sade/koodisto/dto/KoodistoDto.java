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
    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private String codesGroupUri;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Long version;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private int versio;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date paivitysPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date voimassaAlkuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Date voimassaLoppuPvm;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    private Tila tila;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class,JsonViews.Simple.class})
    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

    @JsonView(JsonViews.Extended.class)
    private List<Integer> codesVersions;

    @JsonView({JsonViews.Extended.class})
    protected List<String> withinCodes = new ArrayList<String>();

    @JsonView({JsonViews.Extended.class})
    protected List<String> includesCodes = new ArrayList<String>();

    @JsonView({JsonViews.Extended.class})
    protected List<String> levelsWithCodes = new ArrayList<String>();

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

    public String getCodesGroupUri() {
        return codesGroupUri;
    }

    public void setCodesGroupUri(final String codesGroupUri) {
        this.codesGroupUri = codesGroupUri;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public List<Integer> getCodesVersions() {
        return codesVersions;
    }

    public void setCodesVersions(final List<Integer> codesVersions) {
        this.codesVersions = codesVersions;
    }

    public List<String> getWithinCodes() {
        return withinCodes;
    }

    public void setWithinCodes(final List<String> withinCodes) {
        this.withinCodes = withinCodes;
    }

    public List<String> getIncludesCodes() {
        return includesCodes;
    }

    public void setIncludesCodes(final List<String> includesCodes) {
        this.includesCodes = includesCodes;
    }

    public List<String> getLevelsWithCodes() {
        return levelsWithCodes;
    }

    public void setLevelsWithCodes(final List<String> levelsWithCodes) {
        this.levelsWithCodes = levelsWithCodes;
    }
}
