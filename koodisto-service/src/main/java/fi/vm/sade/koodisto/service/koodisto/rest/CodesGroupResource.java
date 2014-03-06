package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("codesgroup")
@PreAuthorize("isAuthenticated()")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response insert(KoodistoDto codesDTO) {
        List<String> codesGroupUris = new ArrayList();
        codesGroupUris.add(codesDTO.getCodesGroupUri());
        try {
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu lisättyä. ", e);

        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
