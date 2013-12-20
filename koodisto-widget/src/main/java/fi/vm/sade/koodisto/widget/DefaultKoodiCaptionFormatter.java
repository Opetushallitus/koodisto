/**
 *
 */
package fi.vm.sade.koodisto.widget;

import java.io.Serializable;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.util.KoodistoHelper;

/**
 * @author tommiha
 *
 */
public class DefaultKoodiCaptionFormatter implements CaptionFormatter<KoodiType>, Serializable {

    private static final long serialVersionUID = 1971775132799729459L;

    @Override
    public String formatCaption(KoodiType koodiDTO) {
        KoodiMetadataType metadata = KoodistoHelper.getKoodiMetadataForLanguage(koodiDTO, KoodistoHelper.getKieliForLocale(I18N.getLocale()));
        if (metadata != null) {
            return metadata.getNimi();
        } else {
            return koodiDTO.getKoodiArvo();
        }
    }

}

