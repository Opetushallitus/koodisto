package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.impl.conversion.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiTilaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;

@Component
@Path("/codeelement")
@PreAuthorize("isAuthenticated()")
@Api(value = "/rest/codeelement", description = "Koodit")
public class CodeElementResource {
    private final static Logger logger = LoggerFactory.getLogger(CodeElementResource.class);

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private KoodistoConfiguration koodistoConfiguration;

    // ////
    // GET

    @GET
    @Path("/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Palauttaa koodiversiot tietystä koodista",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public List<SimpleKoodiDto> getAllCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
        return conversionService.convertAll(codeElements, SimpleKoodiDto.class);
    }

    @GET
    @Path("/{codeElementUri}/{codeElementVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Palauttaa tietyn koodiversion",
            notes = "",
            response = ExtendedKoodiDto.class)
    public ExtendedKoodiDto getCodeElementByUriAndVersion(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathParam("codeElementVersion") int codeElementVersion) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeElementUri, codeElementVersion);
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);

        return conversionService.convert(codeElements.get(0), ExtendedKoodiDto.class);
    }

    @GET
    @Path("/{codesUri}/{codesVersion}/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = KoodiDto.class)
    public KoodiDto getCodeElementByCodeElementUri(
            @ApiParam(value = "Koodiston URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion,
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
        return conversionService.convert(codeElement, KoodiDto.class);
    }

    @GET
    @Path("/codes/{codesUri}/{codesVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public List<SimpleKoodiDto> getAllCodeElementsByCodesUriAndVersion(
            @ApiParam(value = "Koodisto URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathParam("codesVersion") int codesVersion) {
        List<KoodiVersioWithKoodistoItem> codeElements = null;
        if (codesVersion == 0) {
            codeElements = koodiBusinessService.getKoodisByKoodisto(codesUri, false);
        } else {
            codeElements = koodiBusinessService.getKoodisByKoodistoVersio(codesUri, codesVersion, false);
        }
        return conversionService.convertAll(codeElements, SimpleKoodiDto.class);
    }

    @GET
    @Path("/latest/{codeElementUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Palauttaa uusimman koodiversion",
            notes = "",
            response = KoodiDto.class)
    public KoodiDto getLatestCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
        return conversionService.convert(codeElements.get(0), KoodiDto.class);
    }

    // /////
    // POST

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
            @ApiParam(value = "Koodin URI") @PathParam("codesUri") String codesUri,
            @ApiParam(value = "Koodi") KoodiDto codeelementDTO) {
        try {
            KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                    convertFromDTOToCreateKoodiDataType(codeelementDTO));
            KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter = new KoodiVersioWithKoodistoItemToKoodiDtoConverter();
            koodiVersioWithKoodistoItemToKoodiDtoConverter.setKoodistoConfiguration(koodistoConfiguration);

            return Response.status(Response.Status.CREATED).entity(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem)).build();
        } catch (Exception e) {
            logger.warn("Koodia ei saatu lisättyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
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
    public void addRelation(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Linkitettävän koodin URI") @PathParam("codeElementUriToAdd") String codeElementUriToAdd,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {

        koodiBusinessService.addRelation(codeElementUri, codeElementUriToAdd,
                SuhteenTyyppi.valueOf(relationType));
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
    public void removeRelation(
            @ApiParam(value = "Koodin URI") @PathParam("codeElementUri") String codeElementUri,
            @ApiParam(value = "Irroitettavan koodin URI") @PathParam("codeElementUriToRemove") String codeElementUriToRemove,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathParam("relationType") String relationType) {

        koodiBusinessService.removeRelation(codeElementUri, Arrays.asList(codeElementUriToRemove),
                SuhteenTyyppi.valueOf(relationType));
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
            koodiBusinessService.delete(codeElementUri, codeElementVersion);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            logger.warn("Koodia ei saatu poistettua. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ////
    // PUT

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

            KoodiVersioWithKoodistoItem koodiVersio =
                    koodiBusinessService.updateKoodi(convertFromDTOToUpdateKoodiDataType(codeElementDTO));
            return Response.status(Response.Status.CREATED).entity
                    (conversionService.convert(koodiVersio, KoodiDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodia ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // ////////
    // METHODS

    private UpdateKoodiDataType convertFromDTOToUpdateKoodiDataType(KoodiDto koodiDto) {
        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(koodiDto.getVoimassaAlkuPvm());
        XMLGregorianCalendar startDate = null;
        XMLGregorianCalendar endDate = null;

        try {
            startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodiDto.getVoimassaLoppuPvm() != null) {
                c.setTime(koodiDto.getVoimassaLoppuPvm());
                endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        } catch (DatatypeConfigurationException e) {
            logger.warn("Date couldn't be parsed: ", e);
        }

        updateKoodiDataType.setVoimassaAlkuPvm(startDate);
        updateKoodiDataType.setVoimassaLoppuPvm(endDate);
        updateKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());
        updateKoodiDataType.setKoodiUri(koodiDto.getKoodiUri());
        updateKoodiDataType.setVersio(koodiDto.getVersio());
        updateKoodiDataType.setLockingVersion(koodiDto.getVersion());

        if (!koodiDto.getTila().toString().equals("HYVAKSYTTY")) {
            updateKoodiDataType.setTila(UpdateKoodiTilaType.fromValue(koodiDto.getTila().toString()));
        }
        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            updateKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return updateKoodiDataType;
    }

    private CreateKoodiDataType convertFromDTOToCreateKoodiDataType(KoodiDto koodiDto) {
        CreateKoodiDataType createKoodiDataType = new CreateKoodiDataType();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(koodiDto.getVoimassaAlkuPvm());
        XMLGregorianCalendar startDate = null;
        XMLGregorianCalendar endDate = null;
        try {
            startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodiDto.getVoimassaLoppuPvm() != null) {
                c.setTime(koodiDto.getVoimassaLoppuPvm());
                endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        } catch (DatatypeConfigurationException e) {
            logger.warn("Date couldn't be parsed: ", e);
        }
        createKoodiDataType.setVoimassaAlkuPvm(startDate);
        createKoodiDataType.setVoimassaLoppuPvm(endDate);
        createKoodiDataType.setKoodiArvo(koodiDto.getKoodiArvo());

        for (KoodiMetadata koodiMetadata : koodiDto.getMetadata()) {
            createKoodiDataType.getMetadata().add(conversionService.convert(koodiMetadata, KoodiMetadataType.class));
        }

        return createKoodiDataType;
    }
}
