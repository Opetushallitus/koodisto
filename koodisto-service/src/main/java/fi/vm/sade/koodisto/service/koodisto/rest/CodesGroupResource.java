package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.generic.service.exception.SadeBusinessException;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodesGroupValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import org.springframework.stereotype.Controller;

@Controller
@Path("/codesgroup")
@Api(value = "/rest/codesgroup", description = "Koodistoryhmät")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @Autowired
    private KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private UriTransliterator uriTransliterator;

    private CodesGroupValidator codesGroupValidator = new CodesGroupValidator();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei löydy kyseisellä id:llä")
    })
    public Response getCodesByCodesUri(
            @ApiParam(value = "Koodistoryhmän id") @PathParam("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
            return Response.status(Response.Status.OK).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.warn("Error finding CodesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodistoryhmää",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu päivitettyä")
    })
    public Response update(
            @ApiParam(value = "Koodistoryhmä") KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.UPDATE);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid input for updating codesGroup. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.warn("Error while updating codesGroup. ", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Luo uuden koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu lisättyä")
    })
    public Response insert(
            @ApiParam(value = "Koodistoryhmä") KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.INSERT);
            codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata((Collection) codesGroupDTO.getKoodistoRyhmaMetadatas()));
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. codesGroupDTO: " + codesGroupDTO);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.warn("Error while inserting codesGroup.", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen."),
            @ApiResponse(code = 500, message = "Koodiryhmää ei saatu poistettua")
    })
    public Response delete(
            @ApiParam(value = "Koodistoryhmän URI") @PathParam("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            koodistoRyhmaBusinessService.delete(id);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.warn("Error while removing codesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
