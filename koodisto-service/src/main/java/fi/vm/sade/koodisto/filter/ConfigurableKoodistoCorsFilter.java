package fi.vm.sade.koodisto.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 *
 * Calling JavaScript should set withCredentials flag to true when calling REST-methods with @PreAuthorize annotation.
 * Otherwise requests from Chrome (36.0.1985.125) and Mozilla Firefox (31.0) will fail with status 401.
 *
 */
@Component
public class ConfigurableKoodistoCorsFilter extends KoodistoCorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        CorsFiller<ContainerResponseContext, ContainerRequestContext> filler = new KoodistoCorsFiller(mode, allowedDomains);
        filler.setHeadersToResponse(requestContext, responseContext);
        filler.setAllowOrigin(responseContext, requestContext);
    }

    private class KoodistoCorsFiller extends CorsFiller<ContainerResponseContext, ContainerRequestContext> {

        private KoodistoCorsFiller(CorsFilterMode mode, String allowedDomains) {
            super(mode, allowedDomains);
        }

        @Override
        protected void setHeader(String key, String value, ContainerResponseContext response) {
            response.getHeaders().add(key, value);
        }

        @Override
        protected List<String> getHeaders(String key, ContainerRequestContext request) {
            List<String> headers = request.getHeaders().get(key);
            return headers != null ? headers : new ArrayList<>();
        }

    }

}
