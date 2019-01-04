package fi.vm.sade.koodisto.service.koodisto.rest;

import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Controller
@Path("")
public class RedirectResource {
    @GET
    @Path("/swagger/index.html")
    public Response swaggerRedirect() {
        return Response.temporaryRedirect(URI.create("/rest/api-docs?url=/koodisto-service/rest/swagger.json")).build();
    }
    @GET
    @Path("/swagger")
    public Response swaggerRootRedirect() {
        return Response.temporaryRedirect(URI.create("/rest/api-docs?url=/koodisto-service/rest/swagger.json")).build();
    }
}
