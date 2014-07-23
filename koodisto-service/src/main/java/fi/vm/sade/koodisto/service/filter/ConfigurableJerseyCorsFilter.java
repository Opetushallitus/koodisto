package fi.vm.sade.koodisto.service.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public class ConfigurableJerseyCorsFilter implements ContainerResponseFilter {

    private static final String CORSFILTER_MODE_PARAM = "${common.corsfilter.mode:DEVELOPMENT}";
 
    private CorsFilterMode mode;

    @Value("${common.corsfilter.allowed-domains:}")
    private String allowedDomains;

    @Value(CORSFILTER_MODE_PARAM)
    void setMode(String mode) {
        this.mode = StringUtils.isNotBlank(mode) && !mode.equalsIgnoreCase(CORSFILTER_MODE_PARAM) ? CorsFilterMode.valueOf(mode) : CorsFilterMode.DEVELOPMENT;
    }

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        CorsFiller<ContainerResponse, ContainerRequest> filler = new JerseyCorsFiller(mode, allowedDomains);
        filler.copyHeadersToResponse(containerRequest, containerResponse);
        filler.setAllowOrigin(containerResponse, containerRequest);
        return containerResponse;
    }

    private class JerseyCorsFiller extends CorsFiller<ContainerResponse, ContainerRequest> {
        
        private JerseyCorsFiller(CorsFilterMode mode, String allowedDomains) {
            super(mode, allowedDomains);
        }

        @Override
        protected void setHeader(String key, String value, ContainerResponse response) {
            response.getHttpHeaders().add(key, value);
        }

        @Override
        protected List<String> getHeaders(String key, ContainerRequest request) {
            List<String> headers = request.getRequestHeader(key);
            return headers != null ? headers : new ArrayList<String>();
        }

    }

}
