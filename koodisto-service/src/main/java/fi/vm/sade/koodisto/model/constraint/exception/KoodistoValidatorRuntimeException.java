package fi.vm.sade.koodisto.model.constraint.exception;

/*
 * Just a wrapper for RuntimeException for Sonar analysis.
 */

public class KoodistoValidatorRuntimeException extends RuntimeException {

    public KoodistoValidatorRuntimeException(String string) {
        super(string);
    }

    public KoodistoValidatorRuntimeException(Exception e) {
        super(e);
    }

}
