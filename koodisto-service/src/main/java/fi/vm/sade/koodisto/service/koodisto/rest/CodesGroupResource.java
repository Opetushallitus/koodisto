package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Component
@Path("codesgroup")
@PreAuthorize("isAuthenticated()")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @Autowired
    private KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private UriTransliterator uriTransliterator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response insert(KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata((Collection)codesGroupDTO.getKoodistoRyhmaMetadatas()));
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoryhmää ei saatu lisättyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public Response getCodesByCodesUri(@PathParam("id") Long id) {
        try {
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
            return Response.status(Response.Status.OK).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoryhmää ei saatu haettua. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public Response update(KoodistoRyhmaDto codesGroupDTO) {
        try {
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoryhmää ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Simple.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response delete(@PathParam("id") Long id) {
        try {
            koodistoRyhmaBusinessService.delete(id);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            logger.warn("Koodiryhmää ei saatu poistettua. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
