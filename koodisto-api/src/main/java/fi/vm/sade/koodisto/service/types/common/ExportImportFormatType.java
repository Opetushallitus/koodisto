
package fi.vm.sade.koodisto.service.types.common;

public enum ExportImportFormatType {

    JHS_XML,
    CSV,
    XLS;

    public String value() {
        return name();
    }

    public static ExportImportFormatType fromValue(String v) {
        return valueOf(v);
    }

}
