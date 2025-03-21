package fi.vm.sade.koodisto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class RequestCallerFilter extends GenericFilterBean {
    public static final String CALLER_HENKILO_OID_ATTRIBUTE = RequestCallerFilter.class.getName() + ".callerHenkiloOid";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            getJwtToken(servletRequest).ifPresent(jwt -> {
                var username = jwt.getName();
                MDC.put(CALLER_HENKILO_OID_ATTRIBUTE, username);
                servletRequest.setAttribute(CALLER_HENKILO_OID_ATTRIBUTE, username);
            });
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(CALLER_HENKILO_OID_ATTRIBUTE);
        }
    }

    private Optional<JwtAuthenticationToken> getJwtToken(ServletRequest servletRequest) {
        if (servletRequest instanceof HttpServletRequest request) {
            var principal = request.getUserPrincipal();
            if (principal instanceof JwtAuthenticationToken token) {
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }
}