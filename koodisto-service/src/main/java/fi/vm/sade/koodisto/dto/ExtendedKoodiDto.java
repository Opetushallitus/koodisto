package fi.vm.sade.koodisto.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonView;

import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.util.DateCloner;

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
    protected List<RelationCodeElement> withinCodeElements = new ArrayList<RelationCodeElement>();

    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodeElement> includesCodeElements = new ArrayList<RelationCodeElement>();

    @JsonView({JsonViews.Extended.class})
    protected List<RelationCodeElement> levelsWithCodeElements = new ArrayList<RelationCodeElement>();


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

    public List<RelationCodeElement> getWithinCodeElements() {
        return withinCodeElements;
    }

    public void setWithinCodeElements(final List<RelationCodeElement> withinCodeElements) {
        this.withinCodeElements = withinCodeElements;
    }

    public List<RelationCodeElement> getIncludesCodeElements() {
        return includesCodeElements;
    }

    public void setIncludesCodeElements(final List<RelationCodeElement> includesCodeElements) {
        this.includesCodeElements = includesCodeElements;
    }

    public List<RelationCodeElement> getLevelsWithCodeElements() {
        return levelsWithCodeElements;
    }

    public void setLevelsWithCodeElements(final List<RelationCodeElement> levelsWithCodeElements) {
        this.levelsWithCodeElements = levelsWithCodeElements;
    }
    
    public static class RelationCodeElement {
        @JsonView({JsonViews.Extended.class})
        public final String codeElementUri;
        @JsonView({JsonViews.Extended.class})
        public final Integer codeElementVersion;
        @JsonView({JsonViews.Extended.class})
        public final String codeElementValue;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        public final List<SimpleMetadataDto> relationMetadata;
        @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
        public final List<SimpleMetadataDto> parentMetadata;
        
        public RelationCodeElement(String codeElementUri, Integer codeElementVersion, String codeElementValue, List<SimpleMetadataDto> relationMetadata, List<SimpleMetadataDto> parentMetadata) {
            this.codeElementUri = codeElementUri;
            this.codeElementVersion = codeElementVersion;
            this.relationMetadata = relationMetadata;
            this.parentMetadata = parentMetadata;
            this.codeElementValue = codeElementValue;
        }
    }

}
