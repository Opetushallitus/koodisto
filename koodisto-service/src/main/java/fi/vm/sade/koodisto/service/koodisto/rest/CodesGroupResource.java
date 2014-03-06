package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("codesgroup")
@PreAuthorize("isAuthenticated()")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @Autowired
    private KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response insert(KoodistoRyhmaDto codesGroupDTO) {
        try {
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoRyhma,KoodistoRyhmaDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoryhmää ei saatu lisättyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
