package fi.vm.sade.koodisto.ui.util;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA. User: aukil1 Date: 8/18/13 Time: 8:53 PM To
 * change this template use File | Settings | File Templates.
 */
public class KoodiTypeUtil {
    
    private KoodiTypeUtil(){
        // Never called
    }

    public static String extractNameForKoodi(KoodiType koodi) {
        KoodiMetadataType koodiMetadata = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KoodistoHelper.getKieliForLocale(I18N.getLocale()));
        String koodiCaption = koodi.getKoodiArvo() + " N/A";

        if (koodiMetadata == null) {
            koodiMetadata = KoodistoHelper.getKoodiMetadataForAnyLanguage(koodi);
        }

        if (koodiMetadata != null && StringUtils.isNotBlank(koodiMetadata.getNimi())) {
            koodiCaption = koodi.getKoodiArvo() + " " + koodiMetadata.getNimi();
        }
        koodiCaption += " v. " + koodi.getVersio();
        return koodiCaption;
    }

}
