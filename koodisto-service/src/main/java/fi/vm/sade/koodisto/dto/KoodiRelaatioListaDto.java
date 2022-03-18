package fi.vm.sade.koodisto.dto;

import java.util.List;

/**
 * User: petur1
 * Date: 26.6.2014
 */
public class KoodiRelaatioListaDto {

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private String codeElementUri;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private String relationType;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private boolean isChild;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    private List<String> relations;

    public String getCodeElementUri() {
        return codeElementUri;
    }

    public void setCodeElementUri(String codeElementUri) {
        this.codeElementUri = codeElementUri;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean isChild) {
        this.isChild = isChild;
    }

    public List<String> getRelations() {
        return relations;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

}
