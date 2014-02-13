package fi.vm.sade.koodisto.service.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.springframework.beans.factory.annotation.Value;

/**
 * User: kwuoti Date: 15.4.2013 Time: 8.46
 */
public class CorsFilter implements ContainerResponseFilter {
    @Value("${auth.mode}")
    private String authMode;
    
    @Value("${cors.allow-origin}")
    private String allowOrigin;

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
        if ("dev".equals(authMode)) {
            // When testing on localhost, allow script access from all domains
            containerResponse.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        } else {
            // Otherwise, allow only configured domains. Don't add header if no allowed domains
            // configured (allows only same domain).
            if (allowOrigin != null && !allowOrigin.isEmpty()) {
                containerResponse.getHttpHeaders().add("Access-Control-Allow-Origin", allowOrigin);
            }
        }
        return containerResponse;
    }
}
