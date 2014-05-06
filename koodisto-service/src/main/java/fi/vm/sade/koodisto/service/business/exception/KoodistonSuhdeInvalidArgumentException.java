package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistonSuhdeInvalidArgumentException extends
		SadeBusinessException {

	private static final long serialVersionUID = -7155670561938899158L;
	
	public static final String ERROR_KEY = KoodistonSuhdeInvalidArgumentException.class.getCanonicalName();
	
	public KoodistonSuhdeInvalidArgumentException(String message) {
		super(message);
	}
	
	@Override
	public String getErrorKey() {
		return ERROR_KEY;
	}

}
