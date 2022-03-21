
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateKoodiDataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodiArvo;

    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    private List<KoodiMetadataType> metadata;

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String value) {
        this.koodiArvo = value;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date value) {
        this.voimassaAlkuPvm = value;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date value) {
        this.voimassaLoppuPvm = value;
    }

    public List<KoodiMetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

}
