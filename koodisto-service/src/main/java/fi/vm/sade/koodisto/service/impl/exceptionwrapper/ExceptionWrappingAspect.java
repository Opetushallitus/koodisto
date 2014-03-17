package fi.vm.sade.koodisto.service.impl.exceptionwrapper;

import fi.vm.sade.generic.service.exception.AbstractFaultWrapper;
import fi.vm.sade.generic.service.exception.SadeBusinessException;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.types.common.FieldErrorType;
import fi.vm.sade.koodisto.service.types.common.GenericFaultInfoType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Aspect
public class ExceptionWrappingAspect extends AbstractFaultWrapper<GenericFault> {

    public ExceptionWrappingAspect() {
        super(GenericFault.class);
    }

    /**
     * Defines the pointcut for service interface methods.
     */
    @Pointcut("within(fi.vm.sade.koodisto.service.impl.*)")
    public void serviceMethod() {
    }

    /**
     * Around-type advice which simply proceeds to join point but catches thrown
     * exceptions and wraps them.
     * 
     * @param pjp
     * @return
     * @throws GenericFault
     */
    @Around("serviceMethod()")
    public Object wrapException(ProceedingJoinPoint pjp) throws GenericFault {
        return super.wrapException(pjp);
    }

    @Override
    protected GenericFault createFaultInstance(Throwable ex) {
        GenericFaultInfoType info = new GenericFaultInfoType();
        String message = ex.getMessage();
        String key = ex.getClass().getName();
        String explanation = ex.getMessage();
        if (ex instanceof SadeBusinessException) {
            key = ((SadeBusinessException) ex).getErrorKey();
        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException ce = (ConstraintViolationException) ex;
            for (ConstraintViolation<?> cv : ce.getConstraintViolations()) {
                String field = cv.getPropertyPath().toString();
                String fieldErrorMessage = cv.getMessage();

                FieldErrorType fieldErrorType = new FieldErrorType();
                fieldErrorType.setErrorMessage(fieldErrorMessage);
                fieldErrorType.setField(field);
                info.getFieldErrors().add(fieldErrorType);
            }
            message = "Validation failed";
            explanation = "Validation failed";
        }

        info.setErrorCode(key);
        info.setExplanation(explanation);
        return new GenericFault(message, info);
    }

}
