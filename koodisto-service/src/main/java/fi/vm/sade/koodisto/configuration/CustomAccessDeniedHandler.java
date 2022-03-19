package fi.vm.sade.koodisto.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* Tätä voi käyttää tunkkaamaan 403 virheilmoituksia */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    public static final Logger LOG = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null) {
            LOG.warn("User: {} attempted to access the protected URL: {}", auth.getName(), request.getRequestURI());
        }

        response.sendRedirect(request.getContextPath() + "/accessDenied");
    }

}