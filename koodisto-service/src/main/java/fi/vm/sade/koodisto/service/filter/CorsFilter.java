package fi.vm.sade.koodisto.service.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public class CorsFilter implements ContainerResponseFilter {
    
    private static final String CORSFILTER_MODE_PARAM = "${common.corsfilter.mode:DEVELOPMENT}";

    enum Mode { PRODUCTION, DEVELOPMENT};
    
    static final String DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN = "https://virkailija.opintopolku.fi";
    
    private Mode mode;

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
        
        setAllowOrigin(containerResponse);
        return containerResponse;
    }

    private void setAllowOrigin(ContainerResponse containerResponse) {
        if (Mode.DEVELOPMENT.equals(mode)) {
            containerResponse.getHttpHeaders().add("Access-Control-Allow-Origin", "*");           
        } else {
            containerResponse.getHttpHeaders().add("Access-Control-Allow-Origin", DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN);
        }
    }
}
