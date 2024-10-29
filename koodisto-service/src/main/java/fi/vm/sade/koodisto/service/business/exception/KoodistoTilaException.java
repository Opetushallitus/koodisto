/*
 *
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koodisto.service.business.exception;

/**
 * Exception class for situations where koodisto state transition is not allowed
 *
 * @author markusv
 *
 */
public class KoodistoTilaException extends SadeBusinessException {

    private static final long serialVersionUID = 1L;
    public static final String ERROR_KEY = KoodistoTilaException.class.getCanonicalName();
    private static final String ERROR_MESSAGE = "error.codes.state.invalid";

    public KoodistoTilaException() {
        super(ERROR_MESSAGE);
    }

    public KoodistoTilaException(String message, Throwable cause) {
        super(message, cause);
    }

    public KoodistoTilaException(String message) {
        super(message);
    }

    public KoodistoTilaException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    @Override
    public String getErrorKey() {
        return ERROR_KEY;
    }

}