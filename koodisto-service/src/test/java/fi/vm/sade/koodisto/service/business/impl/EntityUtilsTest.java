package fi.vm.sade.koodisto.service.business.impl;

import org.junit.Test;

import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import static org.junit.Assert.assertNotNull;


public class EntityUtilsTest {
    
    @Test
    public void setsStartDateToKoodistoVersioWhenGivenUpdateKoodistoDataTypeWithNullStartDate() {
        KoodistoVersio koodistoVersio = new KoodistoVersio();
        UpdateKoodistoDataType type = new UpdateKoodistoDataType();
        type.setTila(TilaType.HYVAKSYTTY);
        EntityUtils.copyFields(type, koodistoVersio);
        assertNotNull(koodistoVersio.getVoimassaAlkuPvm());
    }
    
}
