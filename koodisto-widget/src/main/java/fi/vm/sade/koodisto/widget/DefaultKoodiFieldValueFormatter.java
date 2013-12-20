/**
 *
 */
package fi.vm.sade.koodisto.widget;

import java.io.Serializable;

import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

/**
 * @author tommiha
 *
 */
public class DefaultKoodiFieldValueFormatter implements FieldValueFormatter<KoodiType>, Serializable {

    private static final long serialVersionUID = 8490598382384623951L;

    /*
     * (non-Javadoc) @see fi.vm.sade.generic.ui.component.FieldValueFormatter#formatFieldValue(fi.vm.sade.koodisto.model.dto.KoodiType)
     */
    @Override
    public Object formatFieldValue(KoodiType koodiDTO) {
        if (koodiDTO == null) {
            return null;
        }

        return koodiDTO.getKoodiArvo();
    }

}

