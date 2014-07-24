package fi.vm.sade.koodisto.service.filter;


import java.util.Arrays;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = "classpath:spring/test-cors-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurableCorsFilterTest {
    
    private static final String DOMAIN1 = "https://test.site.something";

    private static final String DOMAIN2 = "http://ruhtinas.nukettaja.com";

    @Autowired
    private ConfigurableJerseyCorsFilter filter;
    
    private ContainerResponse response;
    private ContainerRequest request;
    private MultivaluedMap<String, Object> responseMap;
    
    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        responseMap = Mockito.mock(MultivaluedMap.class);
        response = Mockito.mock(ContainerResponse.class);
        request = Mockito.mock(ContainerRequest.class);        
        when(request.getRequestHeaders()).thenReturn((MultivaluedMap<String, String>) Mockito.mock(MultivaluedMap.class));
        when(response.getHttpHeaders()).thenReturn(responseMap);
    }
    
    @Test
    public void allowsAccessFromAnywhereInDevelopmentMode() {
        filter.setMode(CorsFilterMode.DEVELOPMENT.name());
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Origin", "*");
    }
    
    @Test
    public void allowsAccessFromAnywhereWhenModeIsNotSet() {
        filter.setMode(null);
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Origin", "*");
    }
    
    @Test
    public void doesNotAllowAccessInProductionModeWhenRequestIsNotInDomainsAllowed() {
        assertDomain("http://something.net", ConfigurableCorsFilter.DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN);
    }
    
    @Test
    public void allowsAccessFromAllowedDomainsOnlyInProductionMode() {
        assertDomain(DOMAIN1, DOMAIN1);
        assertDomain(DOMAIN2, DOMAIN2);
    }
    
    @Test
    public void attempsToDetermineRemoteDomainFromIEsHostHeaderWhenOriginIsNull() {
        filter.setMode(CorsFilterMode.PRODUCTION.name());
        when(request.getRequestHeader("origin")).thenReturn(null);
        when(request.getRequestHeader("host")).thenReturn(Arrays.asList(DOMAIN2));
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Origin", DOMAIN2);
    }
    
    @Test
    public void copiesHeadersFromRequestToResponse()  {
        String headerValue = "value", headerValue2 = "va";
        when(request.getRequestHeader("access-control-request-method")).thenReturn(Arrays.asList(headerValue));
        when(request.getRequestHeader("access-control-request-headers")).thenReturn(Arrays.asList(headerValue2));
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Methods", headerValue);
        verify(responseMap).add("Access-Control-Allow-Headers", headerValue2);
    }

    private void assertDomain(String domain, String expected) {
        when(request.getRequestHeader("origin")).thenReturn(Arrays.asList(domain));
        filter.setMode(CorsFilterMode.PRODUCTION.name());
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Origin", expected);
    }
    
}
