package fi.vm.sade.koodisto.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.18
 */
public class KoodistoDto extends AbstractKoodistoDto {
    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private String codesGroupUri;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private Long version;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private int versio;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private Date paivitysPvm;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private Date voimassaAlkuPvm;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private Date voimassaLoppuPvm;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    private Tila tila;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

    @JsonView(JsonViews.Extended.class)
    private List<Integer> codesVersions;

    @JsonView({ JsonViews.Extended.class })
    protected List<RelationCodes> withinCodes = new ArrayList<RelationCodes>();

    @JsonView({ JsonViews.Extended.class })
    protected List<RelationCodes> includesCodes = new ArrayList<RelationCodes>();

    @JsonView({ JsonViews.Extended.class })
    protected List<RelationCodes> levelsWithCodes = new ArrayList<RelationCodes>();

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

    public List<RelationCodes> getWithinCodes() {
        return withinCodes;
    }

    public void setWithinCodes(final List<RelationCodes> withinCodes) {
        this.withinCodes = withinCodes;
    }

    public List<RelationCodes> getIncludesCodes() {
        return includesCodes;
    }

    public void setIncludesCodes(final List<RelationCodes> includesCodes) {
        this.includesCodes = includesCodes;
    }

    public List<RelationCodes> getLevelsWithCodes() {
        return levelsWithCodes;
    }

    public void setLevelsWithCodes(final List<RelationCodes> levelsWithCodes) {
        this.levelsWithCodes = levelsWithCodes;
    }

    public static class RelationCodes {
        @JsonView({ JsonViews.Extended.class })
        public final String codesUri;
        @JsonView({ JsonViews.Extended.class })
        public final Integer codesVersion;
        @JsonView({JsonViews.Extended.class})
        public final boolean passive;
        
        public RelationCodes() {
            this.codesUri = null;
            this.codesVersion = -1;
            this.passive = false;
        }

        public RelationCodes(String codesUri, Integer codesVersion, boolean passive) {
            this.codesUri = codesUri;
            this.codesVersion = codesVersion;
            this.passive = passive;
        }
    }
}
