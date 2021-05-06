package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.service.types.common.TilaType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TilaTypeFieldHandler extends XmlAdapter<TilaType, String> {

    @Override
    public String unmarshal(TilaType tilaType) {
        return tilaType != null ? tilaType.name() : null;
    }

    @Override
    public TilaType marshal(String s) {
        return s != null ? TilaType.valueOf(s) : null;
    }

}
