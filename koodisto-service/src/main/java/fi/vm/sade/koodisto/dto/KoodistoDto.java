package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;

import java.util.*;

public class KoodistoDto extends AbstractKoodistoDto {
    private String codesGroupUri;

    private Long version;

    private int versio;

    private Date paivitysPvm;

    private String paivittajaOid;

    private Date voimassaAlkuPvm;

    private Date voimassaLoppuPvm;

    private Tila tila;

    private List<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();

    private List<Integer> codesVersions;

    protected List<RelationCodes> withinCodes = new ArrayList<RelationCodes>();

    protected List<RelationCodes> includesCodes = new ArrayList<RelationCodes>();

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
        public final String codesUri;
        public final Integer codesVersion;
        public final boolean passive;
        public final Map<String, String> nimi;

        public RelationCodes() {
            this.codesUri = null;
            this.codesVersion = -1;
            this.passive = false;
            this.nimi = new HashMap<>();
        }

        public RelationCodes(String codesUri, Integer codesVersion, boolean passive, Map<String, String> nimi) {
            this.codesUri = codesUri;
            this.codesVersion = codesVersion;
            this.passive = passive;
            this.nimi = nimi;
        }
    }
}
