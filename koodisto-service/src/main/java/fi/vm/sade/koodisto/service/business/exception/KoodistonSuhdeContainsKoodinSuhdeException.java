package fi.vm.sade.koodisto.service.business.exception;

import fi.vm.sade.generic.service.exception.SadeBusinessException;

public class KoodistonSuhdeContainsKoodinSuhdeException extends
		SadeBusinessException {

	private static final long serialVersionUID = 4713895721067413817L;
	
	public static final String ERROR_KEY = KoodistonSuhdeContainsKoodinSuhdeException.class.getCanonicalName();
	
	public KoodistonSuhdeContainsKoodinSuhdeException(String message) {
		super(message);
	}
	
	@Override
	public String getErrorKey() {
		return ERROR_KEY;
	}

}
