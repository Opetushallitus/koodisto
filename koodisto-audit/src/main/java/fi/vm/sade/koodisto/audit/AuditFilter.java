package fi.vm.sade.koodisto.audit;

import fi.vm.sade.javautils.http.HttpServletRequestUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Fillteri joka lisää lokituksen kontekstiin auditlokituksen tarvitsemia
 * tietoja.
 *
 * Aktivointi: web.xml
 */
public class AuditFilter extends OncePerRequestFilter {

    private static final String KEY_REMOTE_ADDR = "remoteAddr";
    private static final String KEY_SESSION = "session";
    private static final String KEY_USER_AGENT = "userAgent";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        insertIntoMDC(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            clearMDC();
        }
    }

    private static void insertIntoMDC(HttpServletRequest request) {
        insertIntoMdc(KEY_REMOTE_ADDR, HttpServletRequestUtils.getRemoteAddress(request));
        insertIntoMdc(KEY_SESSION, request.getSession().getId());
        insertIntoMdc(KEY_USER_AGENT, request.getHeader("User-Agent"));
    }

    private static void insertIntoMdc(String key, String value) {
        if (value != null) {
            MDC.put(key, value);
        }
    }

    private static void clearMDC() {
        MDC.remove(KEY_REMOTE_ADDR);
        MDC.remove(KEY_SESSION);
        MDC.remove(KEY_USER_AGENT);
    }
}