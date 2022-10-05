package fi.vm.sade.koodisto.util;

public abstract class FieldLengths {

    private FieldLengths() {
        throw new IllegalStateException("Utility class");
    }

    public static final int DEFAULT_FIELD_LENGTH = 256;
    public static final int LONG_FIELD_LENGTH = 2048;
}
