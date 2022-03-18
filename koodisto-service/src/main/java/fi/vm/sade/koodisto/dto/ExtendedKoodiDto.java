package fi.vm.sade.koodisto.dto;

import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.Tila;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtendedKoodiDto {

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

    protected List<RelationCodeElement> withinCodeElements = new ArrayList<RelationCodeElement>();

    protected List<RelationCodeElement> includesCodeElements = new ArrayList<RelationCodeElement>();

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
        public String getCodeElementUri() {
            return codeElementUri;
        }

        public final String codeElementUri;
        public final Integer codeElementVersion;
        public final String codeElementValue;
        public final List<SimpleMetadataDto> relationMetadata;
        public final List<SimpleMetadataDto> parentMetadata;

        public boolean isPassive() {
            return passive;
        }

        public final boolean passive;
        
        public RelationCodeElement() {
            this.codeElementUri = null;
            this.codeElementVersion = -1;
            this.relationMetadata = null;
            this.parentMetadata = null;
            this.codeElementValue = null;
            this.passive = false;
        }

        public RelationCodeElement(String codeElementUri, Integer version, boolean passive) {
            this.codeElementUri = codeElementUri;
            this.codeElementVersion = version;
            this.relationMetadata = null;
            this.parentMetadata = null;
            this.codeElementValue = null;
            this.passive = passive;
            
        }
        
        public RelationCodeElement(String codeElementUri, Integer codeElementVersion, String codeElementValue, List<SimpleMetadataDto> relationMetadata, List<SimpleMetadataDto> parentMetadata, boolean passive) {
            this.codeElementUri = codeElementUri;
            this.codeElementVersion = codeElementVersion;
            this.relationMetadata = relationMetadata;
            this.parentMetadata = parentMetadata;
            this.codeElementValue = codeElementValue;
            this.passive = passive;
        }
    }

}
