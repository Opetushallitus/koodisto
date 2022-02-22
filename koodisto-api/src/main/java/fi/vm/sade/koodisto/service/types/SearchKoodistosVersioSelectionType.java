
package fi.vm.sade.koodisto.service.types;

public enum SearchKoodistosVersioSelectionType {

    ALL,
    LATEST,
    SPECIFIC;

    public String value() {
        return name();
    }

    public static SearchKoodistosVersioSelectionType fromValue(String v) {
        return valueOf(v);
    }

}
