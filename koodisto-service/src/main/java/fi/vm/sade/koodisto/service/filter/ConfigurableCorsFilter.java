package fi.vm.sade.koodisto.service.filter;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public class ConfigurableCorsFilter implements ContainerResponseFilter {
    
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
        if (containerRequest.getRequestHeaders().containsKey("access-control-request-method")) {
            for (String value : containerRequest.getRequestHeaders().get("access-control-request-method")) {
                containerResponse.getHttpHeaders().add("Access-Control-Allow-Methods", value);
            }
        }
        if (containerRequest.getRequestHeaders().containsKey("access-control-request-headers")) {
            for (String value : containerRequest.getRequestHeaders().get("access-control-request-headers")) {
                containerResponse.getHttpHeaders().add("Access-Control-Allow-Headers", value);
            }
        }
        
        setAllowOrigin(containerResponse, containerRequest);
        return containerResponse;
    }

    private void setAllowOrigin(ContainerResponse response, ContainerRequest request) {
        if (Mode.DEVELOPMENT.equals(mode)) {
            response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");           
        } else {
            response.getHttpHeaders().add("Access-Control-Allow-Origin", getMatchingDomain(request));
        }
    }

    private String getMatchingDomain(ContainerRequest request) {
        List<String> headers = request.getRequestHeader("origin");
        headers = headers != null ? headers : request.getRequestHeader("host");
        for (String origin : headers) {
            for(String allowedDomain : allowedDomains.split(" ")) {
                if (allowedDomain.equals(origin)) {
                    return allowedDomain;
                }
            }
        }
        return DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN;
    }
}
