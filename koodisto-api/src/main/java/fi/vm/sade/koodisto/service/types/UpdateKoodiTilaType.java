package fi.vm.sade.koodisto.service.types;

public enum UpdateKoodiTilaType {

    LUONNOS,
    PASSIIVINEN;

    public String value() {
        return name();
    }

    public static UpdateKoodiTilaType fromValue(String v) {
        return valueOf(v);
    }

}
