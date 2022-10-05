
package fi.vm.sade.koodisto.service.types.common;

public enum KieliType {

    FI,
    SV,
    EN;

    public String value() {
        return name();
    }

    public static KieliType fromValue(String v) {
        return valueOf(v);
    }

}
