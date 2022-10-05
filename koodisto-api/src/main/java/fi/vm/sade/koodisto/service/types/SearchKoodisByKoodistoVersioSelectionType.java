
package fi.vm.sade.koodisto.service.types;

public enum SearchKoodisByKoodistoVersioSelectionType {

    LATEST,
    SPECIFIC;

    public String value() {
        return name();
    }

    public static SearchKoodisByKoodistoVersioSelectionType fromValue(String v) {
        return valueOf(v);
    }

}
