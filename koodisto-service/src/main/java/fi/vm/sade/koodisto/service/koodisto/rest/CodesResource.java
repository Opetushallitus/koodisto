package fi.vm.sade.koodisto.service.koodisto.rest;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.generic.service.exception.SadeBusinessException;
import fi.vm.sade.koodisto.dto.FileDto;
import fi.vm.sade.koodisto.dto.FileFormatDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.service.UploadService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.impl.stream.TemporaryFileInputStream;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodesValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

@Component
@Path("/codes")
@Api(value = "/rest/codes", description = "Koodistot")
public class CodesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesResource.class);

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private CodesResourceConverter converter;

    private CodesValidator codesValidator = new CodesValidator();

    @POST
    @Path("/addrelation/{codesUri}/{codesUriToAdd}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää relaatio koodistojen välille",
            notes = "")
    public Response addRelation(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Linkitettävän koodiston URI") @PathParam("codesUriToAdd") String codesUriToAdd,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {
        try {
            String[] errors = { "codesUri", "codesUriToAdd", "relationType" };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToAdd, relationType);

            koodistoBusinessService.addRelation(codesUri, codesUriToAdd, SuhteenTyyppi.valueOf(relationType));
            return Response.status(Response.Status.CREATED).build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: addRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Adding relation to codes failed. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/removerelation/{codesUri}/{codesUriToRemove}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa relaatio koodistojen väliltä",
            notes = "")
    public Response removeRelation(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Irrotettavan koodiston URI") @PathParam("codesUriToRemove") String codesUriToRemove,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {

        try {
            String[] errors = { "codesUri", "codesUriToRemove", "relationType" };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToRemove, relationType);

            koodistoBusinessService.removeRelation(codesUri, Arrays.asList(codesUriToRemove), SuhteenTyyppi.valueOf(relationType));
            return Response.status(Response.Status.CREATED).build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: removeRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Removing relation from codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodistoa",
            notes = "",
            response = Response.class)
    public Response update(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(converter.convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(koodistoVersio.getVersio()).build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: update. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Updating codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @PUT
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodiston kokonaisuutena",
            notes = "Lisää ja poistaa koodistonsuhteita",
            response = Response.class)
    public Response save(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);

            KoodistoVersio koodistoVersio = koodistoBusinessService.saveKoodisto(codesDTO);
            return Response.status(Response.Status.OK).entity(koodistoVersio.getVersio()).build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: save. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Saving codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää koodiston",
            notes = "",
            response = Response.class)
    public Response insert(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.INSERT);
            List<String> codesGroupUris = new ArrayList<String>();
            codesGroupUris.add(codesDTO.getCodesGroupUri());
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, converter.convertFromDTOToCreateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoVersio, KoodistoDto.class)).build();
        
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: insert. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Inserting codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Simple.class)
    @ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät",
            notes = "",
            response = KoodistoRyhmaListDto.class,
            responseContainer = "List")
    public Response listAllCodesGroups() {
        try {
         
            return Response.status(Response.Status.OK).entity(conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class)).build();

        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Listing all codes groups failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot",
            notes = "",
            response = KoodistoVersioListDto.class,
            responseContainer = "List")
    public Response listAllCodesInAllCodeGroups() {
        try {
         
            SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
            return Response.status(Response.Status.OK).entity(conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchType), KoodistoVersioListDto.class)).build();

        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Listing all codes in all codes groups failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/{codesUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodiston",
            notes = "",
            response = KoodistoListDto.class)
    public Response getCodesByCodesUri(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri) {
        try {
            String[] errors = { "codesUri" };
            ValidatorUtil.validateArgs(errors, codesUri);
            
            Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);

            return Response.status(Response.Status.OK).entity(conversionService.convert(koodisto, KoodistoListDto.class)).build();
        
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Getting codes by codes uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    @Path("/{codesUri}/{codesVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @ApiOperation(
            value = "Palauttaa tietyn koodistoversion",
            notes = "",
            response = KoodistoListDto.class)
    public Response getCodesByCodesUriAndVersion(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston vesio") @PathParam("codesVersion") int codesVersion) {
        try {
            String[] errors = { "fileFormat", "codesVersion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);

            KoodistoVersio koodistoVersio = null;
            if (codesVersion == 0) {
                koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(codesUri);
            } else {
                koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri, codesVersion);
            }

            return Response.status(Response.Status.OK).entity(conversionService.convert(koodistoVersio, KoodistoDto.class)).build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUriAndVersion. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Getting codes by codes uri and version failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/upload/{codesUri}")
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Tuo koodiston tiedostosta",
            notes = "",
            response = Response.class)
    public Response uploadFile(
            @ApiParam(value = "Tuotava tiedosto") @FormDataParam("uploadedFile") InputStream fileInputStream,
            @ApiParam(value = "Tiedostotyyppi") @FormDataParam("fileFormat") String fileFormat,
            @ApiParam(value = "Tiedoston koodaus") @FormDataParam("fileEncoding") String fileEncoding,
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri) {
        try {
            String[] errors = { "fileInputStream", "fileFormat", "codesUri" };
            ValidatorUtil.validateArgs(errors, fileInputStream, fileFormat, codesUri);
            if (StringUtils.isBlank(fileEncoding) && Format.valueOf(fileFormat) != Format.XLS) {
                // Encoding can be empty if filetype is binary XLS
                throw new KoodistoValidationException("error.validation.fileEncoding");
            }

            String mime = "";
            ExportImportFormatType formatStr = null;

            String encoding = fileEncoding;
            if (StringUtils.isBlank(encoding) || !Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }

            switch (Format.valueOf(fileFormat)) {
            case JHS_XML:
                formatStr = ExportImportFormatType.JHS_XML;
                mime = "application/xml";
                break;
            case CSV:
                mime = "application/octet-stream; charset=" + encoding;
                formatStr = ExportImportFormatType.CSV;
                break;
            case XLS:
                formatStr = ExportImportFormatType.XLS;
                mime = "application/vnd.ms-excel";
                break;
            }

            DataSource ds = new InputStreamDataSource(fileInputStream, mime);
            DataHandler handler = new DataHandler(ds);
            uploadService.upload(codesUri, formatStr, encoding, handler);
            return Response.status(Response.Status.ACCEPTED).entity("OK").build();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: uploadFile. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Uploading codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }

    }

    @GET
    @Path("/download/{codesUri}/{codesVersion}/{fileFormat}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lataa koodiston CSV, XML tai XLS tiedostona.",
            notes = "Palauttaa tyhjän koodistopohjan, jos koodiston URI on 'blankKoodistoDocument' ja versio on -1.",
            response = Response.class)
    public Response download(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion,
            @ApiParam(value = "Tiedostotyyppi (JHS_XML, CSV, XLS)") @PathParam("fileFormat") Format fileFormat,
            @ApiParam(value = "Tiedoston merkistö (UTF-8, ISO-88519-1, ISO-88519-15)") @DefaultValue("UTF-8") @QueryParam("encoding") String encoding) {
        try {
            String[] errors = { "codesUri", "codesVersion", "fileFormat", "encoding" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, fileFormat, encoding);
            
            File file = koodistoBusinessService.downloadFile(codesUri, codesVersion, fileFormat, encoding);
            TemporaryFileInputStream is = null;
            is = new TemporaryFileInputStream(file); // Response will close input stream:
                                                     // https://jersey.java.net/apidocs/2.10/jersey/javax/ws/rs/core/Response.html
            String extension = "";
            switch (fileFormat) {
            case JHS_XML:
                extension = ".xml";
                break;
            case CSV:
                extension = ".csv";
                break;
            case XLS:
                extension = ".xls";
                break;
            }
            ResponseBuilder responseBuilder = Response.ok((Object) is);
            responseBuilder.header("Content-Disposition", "inline; filename=\"" + codesUri + extension + "\"");
            Response response = responseBuilder.build();
            return response;
            
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: downloadFile. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Downloading codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Path("/delete/{codesUri}/{codesVersion}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodiston",
            notes = "",
            response = Response.class)
    public Response delete(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion) {
        try {
            String[] errors = { "codesUri", "codesVersion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            
            koodistoBusinessService.delete(codesUri, codesVersion);
            return Response.status(Response.Status.ACCEPTED).build();
            
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: delete. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Deleting codes failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
