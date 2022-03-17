package fi.vm.sade.koodisto.dto;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.model.JsonViews;

import java.util.List;

/**
 * User: petur1
 * Date: 26.6.2014
 */
public class KoodiRelaatioListaDto {

    private String codeElementUri;

    private String relationType;

    private boolean isChild;

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
