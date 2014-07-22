package fi.vm.sade.koodisto.service.filter;


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

import fi.vm.sade.koodisto.service.filter.ConfigurableCorsFilter.Mode;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = "classpath:spring/test-cors-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurableCorsFilterTest {
    
    @Autowired
    private ConfigurableCorsFilter filter;
    
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
    public void allowsAccessFromAllowedDomainsOnlyInProductionMode() {
        filter.setMode(Mode.PRODUCTION.name());
        filter.filter(request, response);
        verify(responseMap).add("Access-Control-Allow-Origin", "https://test.site.something http://ruhtinas.nukettaja.com");
    }
    
}
