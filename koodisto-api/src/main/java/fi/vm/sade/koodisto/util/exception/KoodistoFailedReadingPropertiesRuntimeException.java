package fi.vm.sade.koodisto.util.exception;

import java.io.IOException;

/*
 * Just a wrapper for RuntimeException for Sonar analysis.
 */

public class KoodistoFailedReadingPropertiesRuntimeException extends RuntimeException {

    public KoodistoFailedReadingPropertiesRuntimeException(String string, IOException e) {
        super(string, e);
    }

}
