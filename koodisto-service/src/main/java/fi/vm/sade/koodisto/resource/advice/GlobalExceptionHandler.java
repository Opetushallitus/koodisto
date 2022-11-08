package fi.vm.sade.koodisto.resource.advice;


import fi.vm.sade.authorization.NotAuthorizedException;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String DEBUG_LOG_MESSAGE = "GlobalExceptionHandler handeled exception";
    private static final String WARN_NOT_HANDELED_MESSAGE = "GlobalExceptionHandler passed this exception through {}";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = e.getMessage();
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation -> builder.append(" ").append(violation.getPropertyPath()).append(": ").append(violation.getMessage()));
            errorMessage = builder.toString().trim();
        }
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("error.method.not.supported");
    }

    @ExceptionHandler(KoodistoNotFoundException.class)
    public ResponseEntity<Object> handleKoodistoNotFoundException(KoodistoNotFoundException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(KoodiNotFoundException.class)
    public ResponseEntity<Object> handleKoodiNotFoundException(KoodiNotFoundException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.badRequest().body("error.http.message.not.readable");
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageConversionException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.badRequest().body("error.http.message.not.convertable");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.badRequest().body("error.mediatype.not.supported");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(MethodArgumentTypeMismatchException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.badRequest().body(String.format("error.validation.%s", e.getParameter().getParameterName()).toLowerCase());
    }

    @ExceptionHandler(SadeBusinessException.class)
    public ResponseEntity<Object> handleSadeBusinessException(SadeBusinessException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(KoodistoRyhmaNotFoundException.class)
    public ResponseEntity<Object> handleSadeBusinessException(KoodistoRyhmaNotFoundException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        List<String> errors = e.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("error.codes.insufficient.access.rights");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        throw e;
    }

    @ExceptionHandler(KoodistoRyhmaNotEmptyException.class)
    public ResponseEntity<Object> handleKoodistoRyhmaNotEmptyException(KoodistoRyhmaNotEmptyException e) {
        log.debug(DEBUG_LOG_MESSAGE, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error.koodistoryhma.not.empty");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) throws Exception {
        log.warn(WARN_NOT_HANDELED_MESSAGE, e.getClass().getName());
        throw e;
    }

}
