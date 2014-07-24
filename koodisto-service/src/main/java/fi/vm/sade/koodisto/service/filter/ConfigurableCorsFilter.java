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

@Component
public class ConfigurableCorsFilter implements Filter {
    
    private static final String CORSFILTER_MODE_PARAM = "${common.corsfilter.mode:DEVELOPMENT}";

    static final String DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN = "https://virkailija.opintopolku.fi";
    
    private CorsFilterMode mode;
    
    @Value("${common.corsfilter.allowed-domains:}")
    private String allowedDomains;

    @Value(CORSFILTER_MODE_PARAM)
    void setMode(String mode) {
        this.mode = StringUtils.isNotBlank(mode) && !mode.equalsIgnoreCase(CORSFILTER_MODE_PARAM) ? CorsFilterMode.valueOf(mode) : CorsFilterMode.DEVELOPMENT;
    }
    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        CorsFiller<HttpServletResponse, HttpServletRequest> filler = new HttpServletCorsFiller(mode, allowedDomains);
        filler.setHeadersToResponse(req, res);
        filler.setAllowOrigin(res, req);
        chain.doFilter(request, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
      
    private class HttpServletCorsFiller extends CorsFiller<HttpServletResponse, HttpServletRequest> {

        protected HttpServletCorsFiller(CorsFilterMode mode, String allowedDomains) {
            super(mode, allowedDomains);
        }

        @Override
        protected void setHeader(String key, String value, HttpServletResponse response) {
            response.addHeader(key, value);
        }

        @Override
        protected List<String> getHeaders(String key, HttpServletRequest request) {
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
