package fi.vm.sade.koodisto.service.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public class ConfigurableCorsFilter implements ContainerResponseFilter, Filter {
    
    private static final String CORSFILTER_MODE_PARAM = "${common.corsfilter.mode:DEVELOPMENT}";

    enum Mode { PRODUCTION, DEVELOPMENT};
    
    static final String DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN = "https://virkailija.opintopolku.fi";
    
    private Mode mode;
    
    @Value("${common.corsfilter.allowed-domains:}")
    private String allowedDomains;

    @Value(CORSFILTER_MODE_PARAM)
    void setMode(String mode) {
        this.mode = StringUtils.isNotBlank(mode) && !mode.equalsIgnoreCase(CORSFILTER_MODE_PARAM) ? Mode.valueOf(mode) : Mode.DEVELOPMENT;
    }
    
    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        CorsFiller<ContainerResponse, ContainerRequest> filler = new JerseyCorsFiller();
        filler.copyHeadersToResponse(containerRequest, containerResponse);
        filler.setAllowOrigin(containerResponse, containerRequest);
        return containerResponse;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        CorsFiller<HttpServletResponse, HttpServletRequest> filler = new HttpServletCorsFiller();
        filler.copyHeadersToResponse(req, res);
        filler.setAllowOrigin(res, req);
        chain.doFilter(request, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
    
    private abstract class CorsFiller<R, Q> {
        
        abstract void setHeader(String key, String value, R response);
        
        abstract List<String> getHeaders(String key, Q request);
        
        private String getRemoteDomain(Q request) {
            List<String> headers = getHeaders("origin", request);
            headers = !headers.isEmpty() ? headers : getHeaders("host", request);
            return !headers.isEmpty() ? headers.get(0) : DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN;
        }
        
        private void setAllowOrigin(R response, Q request) {
            if (Mode.DEVELOPMENT.equals(mode)) {
                setHeader("Access-Control-Allow-Origin", "*", response);           
            } else {
                setHeader("Access-Control-Allow-Origin", getMatchingDomain(request), response);
            }
        }
        
        private void copyHeadersToResponse(Q request, R response) {
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
    
    private class JerseyCorsFiller extends CorsFiller<ContainerResponse, ContainerRequest> {

        @Override
        void setHeader(String key, String value, ContainerResponse response) {
            response.getHttpHeaders().add(key, value);
        }

        @Override
        List<String> getHeaders(String key, ContainerRequest request) {
            List<String> headers = request.getRequestHeader(key);
            return headers != null ? headers : new ArrayList<String>();
        }
        
    }
    
    private class HttpServletCorsFiller extends CorsFiller<HttpServletResponse, HttpServletRequest> {

        @Override
        void setHeader(String key, String value, HttpServletResponse response) {
            response.addHeader(key, value);
        }

        @Override
        List<String> getHeaders(String key, HttpServletRequest request) {
            List<String> headerValues = new ArrayList<String>();
            Enumeration<String> headers = request.getHeaders(key);
            if(headers != null) {
                while (headers.hasMoreElements()) {
                    headerValues.add(headers.nextElement());
                }
            };
            return headerValues;
        }

    }
}
