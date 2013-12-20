package fi.vm.sade.koodisto.util;

import fi.vm.sade.generic.rest.Cacheable;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("/rest/json")
@Produces(MediaType.APPLICATION_JSON)
public class MockKoodistoJsonRESTService {

    private static int counter = 1;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Cacheable(maxAgeSeconds = 2)
    public String listAllKoodistoRyhmas() {
        return "[{\"koodistoRyhmaUri\":\"mockKoodistoRyhma"+(counter++)+"\"},{\"koodistoRyhmaUri\":\"another\"}]";
    }

}
