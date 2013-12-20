package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.generic.rest.Cacheable;
import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.impl.KoodistoRole;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.codehaus.jackson.map.annotate.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Component
@Path("codes")
@PreAuthorize("isAuthenticated()")
public class CodesResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesResource.class);

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({KoodistoRole.UPDATE,KoodistoRole.CRUD})
    @Transactional
    public Response update(KoodistoDto codesDTO) {
        try {
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoVersio,KoodistoDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu päivitettyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private UpdateKoodistoDataType convertFromDTOToUpdateKoodistoDataType(KoodistoDto koodistoDto) {
        UpdateKoodistoDataType updateKoodistoDataType = new UpdateKoodistoDataType();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(koodistoDto.getVoimassaAlkuPvm());
        XMLGregorianCalendar startDate = null;
        XMLGregorianCalendar endDate = null;
        try {
            startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodistoDto.getVoimassaLoppuPvm() != null) {
                c.setTime(koodistoDto.getVoimassaLoppuPvm());
                endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        } catch (DatatypeConfigurationException e) {
            logger.warn("Date couldn't be parsed: ", e);
        }
        updateKoodistoDataType.setVoimassaAlkuPvm(startDate);
        updateKoodistoDataType.setVoimassaLoppuPvm(endDate);
        updateKoodistoDataType.setKoodistoUri(koodistoDto.getKoodistoUri());
        updateKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        updateKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        updateKoodistoDataType.setVersio(koodistoDto.getVersio());
        updateKoodistoDataType.setTila(TilaType.fromValue(koodistoDto.getTila().toString()));
        updateKoodistoDataType.setLockingVersion(koodistoDto.getVersion());
        for (KoodistoMetadata koodistoMetadata : koodistoDto.getMetadata()) {
            updateKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata,KoodistoMetadataType.class));
        }

        return updateKoodistoDataType;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({KoodistoRole.CRUD})
    @Transactional
    public Response insert(KoodistoDto codesDTO) {
        List<String> codesGroupUris = new ArrayList();
        codesGroupUris.add(codesDTO.getCodesGroupUri());
        try {
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, convertFromDTOToCreateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoVersio,KoodistoDto.class)).build();
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu lisättyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private CreateKoodistoDataType convertFromDTOToCreateKoodistoDataType(KoodistoDto koodistoDto) {
        CreateKoodistoDataType createKoodistoDataType = new CreateKoodistoDataType();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(koodistoDto.getVoimassaAlkuPvm());
        XMLGregorianCalendar startDate = null;
        XMLGregorianCalendar endDate = null;
        try {
            startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            if (koodistoDto.getVoimassaLoppuPvm() != null) {
                c.setTime(koodistoDto.getVoimassaLoppuPvm());
                endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            }
        } catch (DatatypeConfigurationException e) {
            logger.warn("Date couldn't be parsed: ", e);
        }
        createKoodistoDataType.setVoimassaAlkuPvm(startDate);
        createKoodistoDataType.setVoimassaLoppuPvm(endDate);
        createKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        createKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        for (KoodistoMetadata koodistoMetadata : koodistoDto.getMetadata()) {
            createKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata,KoodistoMetadataType.class));
        }

        return createKoodistoDataType;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Simple.class)
    @Secured({KoodistoRole.READ, KoodistoRole.UPDATE, KoodistoRole.CRUD})
    @Transactional
    public List<KoodistoRyhmaListDto> listAllCodesGroups() {
        return conversionService.convertAll(
                koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
    }

    @GET
    @Path("group/{codeGroupUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({KoodistoRole.READ, KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public KoodistoRyhmaListDto listAllCodesInCodeGroup(@PathParam("codeGroupUri") String codeGroupUri) {
        return conversionService.convert(koodistoBusinessService.getKoodistoGroup(codeGroupUri), KoodistoRyhmaListDto.class);
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({KoodistoRole.READ, KoodistoRole.UPDATE, KoodistoRole.CRUD})
    public List<KoodistoVersioListDto> listAllCodesInAllCodeGroups() {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
        return conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchType), KoodistoVersioListDto.class);
    }

    @GET
    @Path("{codesUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Basic.class})
    @Secured({KoodistoRole.READ, KoodistoRole.UPDATE, KoodistoRole.CRUD})
    @Transactional
    public KoodistoListDto getCodesByCodesUri(@PathParam("codesUri") String codesUri) {
        Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);

        return conversionService.convert(koodisto, KoodistoListDto.class);
    }

    @GET
    @Path("{codesUri}/{codesVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Extended.class})
    @Secured({KoodistoRole.READ, KoodistoRole.UPDATE, KoodistoRole.CRUD})
    @Transactional
    public KoodistoDto getCodesByCodesUriAndVersion(@PathParam("codesUri") String codesUri, @PathParam("codesVersion") int codesVersion) {
        KoodistoVersio koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri,codesVersion);

        return conversionService.convert(koodistoVersio, KoodistoDto.class);
    }
}
