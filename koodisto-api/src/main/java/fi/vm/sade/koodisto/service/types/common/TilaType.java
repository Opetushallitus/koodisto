package fi.vm.sade.koodisto.service.types.common;

public enum TilaType {

    PASSIIVINEN,
    LUONNOS,
    HYVAKSYTTY;

    public String value() {
        return name();
    }

    public static TilaType fromValue(String v) {
        return valueOf(v);
    }

}
