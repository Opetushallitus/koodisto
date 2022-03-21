
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateKoodistoDataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected String omistaja;
    protected String organisaatioOid;
    protected Boolean lukittu;
    private List<KoodistoMetadataType> metadataList;

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

    public String getOmistaja() {
        return omistaja;
    }

    public void setOmistaja(String value) {
        this.omistaja = value;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String value) {
        this.organisaatioOid = value;
    }

    public Boolean isLukittu() {
        return lukittu;
    }

    public void setLukittu(Boolean value) {
        this.lukittu = value;
    }

    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<>();
        }
        return this.metadataList;
    }
}
