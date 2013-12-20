package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * User: kwuoti
 * Date: 8.4.2013
 * Time: 15.04
 */
public class TilaTypeFieldHandler extends GeneralizedFieldHandler {
    @Override
    public Object convertUponGet(Object o) {
        if (o == null) {
            return null;
        }

        return ((TilaType) o).name();
    }

    @Override
    public Object convertUponSet(Object o) {
        if (o == null) {
            return null;
        }

        return TilaType.valueOf((String) o);
    }

    @Override
    public Class getFieldType() {
        return TilaType.class;
    }
}
