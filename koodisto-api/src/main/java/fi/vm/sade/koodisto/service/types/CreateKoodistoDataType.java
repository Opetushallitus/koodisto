
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateKoodistoDataType implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    protected String omistaja;
    @NotBlank(message = "error.organisaatioOid.blank")
    protected String organisaatioOid;
    protected Boolean lukittu;
    @NotEmpty(message = "error.metadataList.empty")
    private List<KoodistoMetadataType> metadataList;

    public Boolean isLukittu() {
        return lukittu;
    }
    public List<KoodistoMetadataType> getMetadataList() {
        if (metadataList == null) {
            metadataList = new ArrayList<>();
        }
        return this.metadataList;
    }
}
