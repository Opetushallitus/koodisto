package fi.vm.sade.koodisto.ui.koodisto;

public enum Encoding {
    UTF8("UTF-8"), ISO_88519_1("ISO-88519-1"), ISO_88519_15("ISO-88519-15");

    Encoding(String stringValue) {
        this.stringValue = stringValue;
    }

    private String stringValue;

    public String getStringValue() {
        return this.stringValue;
    }

}