
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateKoodiDataType implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected String koodiArvo;

    protected Date voimassaAlkuPvm;
    protected Date voimassaLoppuPvm;
    private List<KoodiMetadataType> metadata;

    public List<KoodiMetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

}
