
package fi.vm.sade.koodisto.service.types;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateKoodiDataType extends CreateKoodiDataType implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String koodiUri;
    protected UpdateKoodiTilaType tila;
    protected int versio;
    protected long lockingVersion;

    @Builder
    public UpdateKoodiDataType(
            //create
            String koodiArvo,
            Date voimassaAlkuPvm,
            Date voimassaLoppuPvm,
            List<KoodiMetadataType> metadata,
            // update
            String koodiUri,
            UpdateKoodiTilaType tila,
            int versio,
            long lockingVersion) {
        super(koodiArvo, voimassaAlkuPvm, voimassaLoppuPvm, metadata);
        this.koodiUri = koodiUri;
        this.tila = tila;
        this.versio = versio;
        this.lockingVersion = lockingVersion;
    }
}
