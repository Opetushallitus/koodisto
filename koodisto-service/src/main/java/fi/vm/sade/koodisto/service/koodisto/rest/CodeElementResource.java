package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.service.business.exception.SadeBusinessException;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.impl.conversion.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodeElementRelationListValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodeElementValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ExtendedCodeElementValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;

@Controller
@Path("/codeelement")
@Api(value = "/rest/codeelement", description = "Koodit")
public class CodeElementResource {
    private final static Logger logger = LoggerFactory.getLogger(CodeElementResource.class);

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    @Autowired
    private KoodiChangesService changesService;
    
    @Autowired
    private CodeElementResourceConverter converter;

    private CodeElementValidator codesValidator = new CodeElementValidator();
    private CodeElementRelationListValidator relationValidator = new CodeElementRelationListValidator();
    private ExtendedCodeElementValidator extendedValidator = new ExtendedCodeElementValidator();

    @GET
    @Path("/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @ApiOperation(
            value = "Palauttaa koodiversiot tietystä koodista",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public Response getAllCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            return Response.status(Response.Status.OK).entity(conversionService.convertAll(codeElements, SimpleKoodiDto.class)).build();

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementVersionsByCodeElementUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement versions by uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/{codeElementUri}/{codeElementVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @Transactional(readOnly = true)
    @ApiOperation(
            value = "Palauttaa tietyn koodiversion",
            notes = "sisältää koodiversion koodinsuhteet",
            response = ExtendedKoodiDto.class)
    public Response getCodeElementByUriAndVersion(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathParam("codeElementVersion") int codeElementVersion) {
        try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeElementUri, codeElementVersion);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);

            if (codeElements.size() == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error.codeelement.not.found").build();
            }
            return Response.status(Response.Status.OK).entity(conversionService.convert(codeElements.get(0), ExtendedKoodiDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByUriAndVersion. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri and version failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/{codesUri}/{codesVersion}/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = KoodiDto.class)
    public Response getCodeElementByCodeElementUri(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion,
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codesuri", "codesversion", "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, codeElementUri);
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codesversion"));

            KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
            return Response.status(Response.Status.OK).entity(conversionService.convert(codeElement, KoodiDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByCodeElementUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/codes/{codesUri}/{codesVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public Response getAllCodeElementsByCodesUriAndVersion(
            @ApiParam(value = "Koodisto URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion) {
        try {
            String[] errors = { "codesuri", "codesversion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            ValidatorUtil.checkForGreaterThan(codesVersion, -1, new KoodistoValidationException("error.validation.codeelementversion"));

            List<KoodiVersioWithKoodistoItem> codeElements = null;
            if (codesVersion == 0) {
                // FIXME: Why return anything when version is invalid?
                codeElements = koodiBusinessService.getKoodisByKoodisto(codesUri, false);
            } else {
                codeElements = koodiBusinessService.getKoodisByKoodistoVersio(codesUri, codesVersion, false);
            }
            return Response.status(Response.Status.OK).entity(conversionService.convertAll(codeElements, SimpleKoodiDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementsByCodesUriAndVersion. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/latest/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa uusimman koodiversion",
            notes = "",
            response = KoodiDto.class)
    public Response getLatestCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            if (codeElements.size() < 1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error.codeelement.not.found").build();
            }
            return Response.status(Response.Status.OK).entity(conversionService.convert(codeElements.get(0), KoodiDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getLatestCodeElementVersionsByCodeElementUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @Path("/changes/{codeElementUri}/{codeElementVersion}")
    @ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodiversioon",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)
    public Response getChangesToCodeElement(@ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathParam("codeElementVersion") Integer codeElementVersion, 
            @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon") @DefaultValue("false") @QueryParam("compareToLatestAccepted") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            KoodiChangesDto dto = changesService.getChangesDto(codeElementUri, codeElementVersion, compareToLatestAccepted);
            return Response.status(Response.Status.OK).entity(dto).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @Path("/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}")
    @ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodiversioon käyttäen lähintä päivämäärään osuvaa koodiversiota vertailussa",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)
    public Response getChangesToCodeElementWithDate(@ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Kuukauden päivä") @PathParam("dayofmonth") Integer dayOfMonth,
            @ApiParam(value = "Kuukausi") @PathParam("month") Integer month,
            @ApiParam(value = "Vuosi") @PathParam("year") Integer year,
            @ApiParam(value = "Tunti") @PathParam("hour") Integer hourOfDay,
            @ApiParam(value = "Minuutti") @PathParam("minute") Integer minute,
            @ApiParam(value = "Sekunti") @PathParam("second") Integer second,
            @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon") @DefaultValue("false") @QueryParam("compareToLatestAccepted") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.validateDateParameters(dayOfMonth, month, year, hourOfDay, minute, second);
            DateTime dateTime = new DateTime(year, month, dayOfMonth, hourOfDay, minute, second);
            KoodiChangesDto dto = changesService.getChangesDto(codeElementUri, dateTime, compareToLatestAccepted);
            return Response.status(Response.Status.OK).entity(dto).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/{codesUri}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää uuden koodin",
            notes = "",
            response = Response.class)
    public Response insert(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodi") KoodiDto codeelementDTO) {
        try {
            String[] errors = { "codesuri" };
            ValidatorUtil.validateArgs(errors, codesUri);
            codesValidator.validate(codeelementDTO, ValidationType.INSERT);

            KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                    converter.convertFromDTOToCreateKoodiDataType(codeelementDTO));
            KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter = new KoodiVersioWithKoodistoItemToKoodiDtoConverter();
            koodiVersioWithKoodistoItemToKoodiDtoConverter.setKoodistoConfiguration(koodistoConfiguration);

            return Response.status(Response.Status.CREATED).entity(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem))
                    .build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: insert. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Inserting codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää relaation koodien välille",
            notes = "")
    public Response addRelation(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Linkitettävän koodin URI") @PathParam("codeElementUriToAdd") String codeElementUriToAdd,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {
        try {
            String[] errors = { "codeelementuri", "codeelementuritoadd", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToAdd, relationType);

            koodiBusinessService.addRelation(codeElementUri, Arrays.asList(codeElementUriToAdd), SuhteenTyyppi.valueOf(relationType), false);
            return Response.status(Response.Status.OK).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding relation to codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/addrelations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää koodien välisiä relaatioita, massatoiminto",
            notes = "")
    public Response addRelations(
            @ApiParam(value = "Relaation tiedot JSON muodossa") KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.INSERT);

            koodiBusinessService.addRelation(koodiRelaatioDto);
            return Response.status(Response.Status.OK).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelations. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding multiple relations to codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodien välisen relaation",
            notes = "")
    public Response removeRelation(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Irroitettavan koodin URI") @PathParam("codeElementUriToRemove") String codeElementUriToRemove,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {

        try {
            String[] errors = { "codeelementuri", "codeelementuritoremove", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToRemove, relationType);

            koodiBusinessService.removeRelation(codeElementUri, Arrays.asList(codeElementUriToRemove),
                    SuhteenTyyppi.valueOf(relationType), false);
            return Response.status(Response.Status.OK).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing relation to codeElement failed failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/removerelations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(value = "Poistaa koodien välisiä relaatioita, massatoiminto", notes = "")
    public Response removeRelations(
            @ApiParam(value = "Relaation tiedot JSON muodossa") KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.UPDATE);

            koodiBusinessService.removeRelation(koodiRelaatioDto);
            return Response.status(Response.Status.OK).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelations. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing multiple relations form codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/delete/{codeElementUri}/{codeElementVersion}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodin",
            notes = "",
            response = Response.class)
    public Response delete(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathParam("codeElementVersion") int codeElementVersion) {
        try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            koodiBusinessService.delete(codeElementUri, codeElementVersion);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: delete. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Deleting the codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodin",
            notes = "",
            response = Response.class)
    public Response update(
            @ApiParam(value = "Koodi") KoodiDto codeElementDTO) {
        try {
            codesValidator.validate(codeElementDTO, ValidationType.UPDATE);
            KoodiVersioWithKoodistoItem koodiVersio =
                    koodiBusinessService.updateKoodi(converter.convertFromDTOToUpdateKoodiDataType(codeElementDTO));
            return Response.status(Response.Status.CREATED).entity
                    (conversionService.convert(koodiVersio, KoodiDto.class)).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: update. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Updating codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodin kokonaisuutena",
            notes = "Lisää ja poistaa koodinsuhteita vastaamaan annettua koodia.",
            response = Response.class)
    public Response save(
            @ApiParam(value = "Koodi") ExtendedKoodiDto koodiDTO) {
        try {
            extendedValidator.validate(koodiDTO, ValidationType.UPDATE);

            KoodiVersio koodiVersio = koodiBusinessService.saveKoodi(koodiDTO);
            return Response.status(Response.Status.OK).entity(koodiVersio.getVersio().toString()).build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: save. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Saving codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
