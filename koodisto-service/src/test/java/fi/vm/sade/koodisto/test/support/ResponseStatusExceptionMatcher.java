package fi.vm.sade.koodisto.test.support;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusExceptionMatcher extends BaseMatcher<Exception> {

    private final HttpStatus status;
    private final String message;

    public ResponseStatusExceptionMatcher(HttpStatus status) {
        this(status, null);
    }

    public ResponseStatusExceptionMatcher(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public boolean matches(Object o) {
        if (o instanceof ResponseStatusException) {
            ResponseStatusException e = (ResponseStatusException) o;
            return e.getStatus() == status
                    && (message == null || message.equals(e.getMessage()));
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches status");
    }
}
