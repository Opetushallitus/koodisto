package fi.vm.sade.koodisto.service.impl.exceptionwrapper;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.types.common.FieldErrorType;
import fi.vm.sade.koodisto.service.types.common.GenericFaultInfoType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Aspect
public class ExceptionWrappingAspect {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExceptionWrappingAspect.class);

    public ExceptionWrappingAspect() {
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

        try {
            return pjp.proceed();
        } catch (Throwable ex) {

            LOGGER.warn("Exception wrapped. ", ex);

            MethodSignature sigu = (MethodSignature) pjp.getSignature();
            Class[] types = sigu.getExceptionTypes();
            Set<Class> classSet = new HashSet<Class>(Arrays.asList(types));

            if (classSet.contains(GenericFault.class)) {
                throw createFaultInstance(ex);
            } else if(ex instanceof KoodiNotFoundException) {
                throw new WebApplicationException(ex, Response.Status.NOT_FOUND);
            } else {
                throw new RuntimeException("Unhandled error: " + ex.getClass() + " - " + ex.getMessage(), ex);
            }
        }

    }

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
