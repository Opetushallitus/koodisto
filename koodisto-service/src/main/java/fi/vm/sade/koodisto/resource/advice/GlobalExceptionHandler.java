package fi.vm.sade.koodisto.resource.advice;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String GENERIC_ERROR_CODE = "error.codes.generic";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage = ex.getMessage();
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation -> builder.append(" ").append(violation.getMessage()));
            errorMessage = builder.toString().trim();
        }
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("error.method.not.supported");
    }

    @ExceptionHandler(KoodistoNotFoundException.class)
    public ResponseEntity<Object> handleKoodistoNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error.koodisto.not.found");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException() {
        return ResponseEntity.badRequest().body("error.http.message.not.readable");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.badRequest().body("error.mediatype.not.supported");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(String.format("error.validation.%s", e.getParameter().getParameterName()).toLowerCase());
    }

    @ExceptionHandler(SadeBusinessException.class)
    public ResponseEntity<Object> handleSadeBusinessException(SadeBusinessException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(KoodistoRyhmaNotFoundException.class)
    public ResponseEntity<Object> handleSadeBusinessException(KoodistoRyhmaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleIllegalArgumentException(Exception e) {
        return ResponseEntity.internalServerError().body(GENERIC_ERROR_CODE);
    }


}
