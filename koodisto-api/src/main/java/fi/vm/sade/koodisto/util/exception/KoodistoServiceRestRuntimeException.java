package fi.vm.sade.koodisto.util.exception;

import java.io.IOException;

/*
 * Just a wrapper for RuntimeException for Sonar analysis.
 */

public class KoodistoServiceRestRuntimeException extends RuntimeException {

    public KoodistoServiceRestRuntimeException(IOException e) {
        super(e);
    }

}
