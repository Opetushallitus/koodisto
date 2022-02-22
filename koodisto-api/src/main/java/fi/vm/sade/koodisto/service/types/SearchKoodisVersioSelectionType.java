
package fi.vm.sade.koodisto.service.types;

public enum SearchKoodisVersioSelectionType {

    ALL,
    LATEST,
    SPECIFIC;

    public String value() {
        return name();
    }

    public static SearchKoodisVersioSelectionType fromValue(String v) {
        return valueOf(v);
    }

}
