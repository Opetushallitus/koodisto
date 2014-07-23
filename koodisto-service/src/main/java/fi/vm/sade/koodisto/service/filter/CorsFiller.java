package fi.vm.sade.koodisto.service.filter;

import java.util.List;

abstract class CorsFiller<R, Q> {
    
    private final CorsFilterMode mode;
    
    private final String allowedDomains;
    
    protected static final String DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN = "https://virkailija.opintopolku.fi";
    
    protected CorsFiller(CorsFilterMode mode, String allowedDomains) {
        this.mode = mode;
        this.allowedDomains = allowedDomains;
    }

    protected abstract void setHeader(String key, String value, R response);

    protected abstract List<String> getHeaders(String key, Q request);

    private String getRemoteDomain(Q request) {
        List<String> headers = getHeaders("origin", request);
        headers = !headers.isEmpty() ? headers : getHeaders("host", request);
        return !headers.isEmpty() ? headers.get(0) : DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN;
    }

    protected void setAllowOrigin(R response, Q request) {
        if (CorsFilterMode.DEVELOPMENT.equals(mode)) {
            setHeader("Access-Control-Allow-Origin", "*", response);           
        } else {
            setHeader("Access-Control-Allow-Origin", getMatchingDomain(request), response);
        }
    }

    protected void copyHeadersToResponse(Q request, R response) {
        for (String value : getHeaders("access-control-request-method", request)) {
            setHeader("Access-Control-Allow-Methods", value, response);
        }
        for (String value : getHeaders("access-control-request-headers", request)) {
            setHeader("Access-Control-Allow-Headers", value, response);
        }
    }

    private String getMatchingDomain(Q request) {
        String domain = getRemoteDomain(request);
        for(String allowedDomain : allowedDomains.split(" ")) {
            if (allowedDomain.equals(domain)) {
                return allowedDomain;
            }
        }
        return DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN;
    }
}
