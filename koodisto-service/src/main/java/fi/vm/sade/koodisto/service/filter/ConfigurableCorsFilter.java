package fi.vm.sade.koodisto.service.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Component
public class ConfigurableCorsFilter extends CorsFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        CorsFiller<ContainerResponse, ContainerRequest> filler = new JerseyCorsFiller(mode, allowedDomains);
        filler.setHeadersToResponse(containerRequest, containerResponse);
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
