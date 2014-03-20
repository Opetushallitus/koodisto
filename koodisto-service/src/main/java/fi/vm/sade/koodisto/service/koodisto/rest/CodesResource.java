package fi.vm.sade.koodisto.service.koodisto.rest;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
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

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.FileDto;
import fi.vm.sade.koodisto.dto.FileFormatDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.service.UploadService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

@Component
@Path("codes")
@PreAuthorize("isAuthenticated()")
public class CodesResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesResource.class);

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private DownloadService downloadService;

    @POST
    @Path("addrelation/{codesUri}/{codesUriToAdd}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public void addRelation(@PathParam("codesUri") String codesUri, @PathParam("codesUriToAdd") String codesUriToAdd,
            @PathParam("relationType") String relationType) {

        koodistoBusinessService.addRelation(codesUri, codesUriToAdd, SuhteenTyyppi.valueOf(relationType));
    }

    @POST
    @Path("removerelation/{codesUri}/{codesUriToRemove}/{relationType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public void removeRelation(@PathParam("codesUri") String codesUri, @PathParam("codesUriToRemove") String codesUriToRemove,
            @PathParam("relationType") String relationType) {

        koodistoBusinessService.removeRelation(codesUri, Arrays.asList(codesUriToRemove), SuhteenTyyppi.valueOf(relationType));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public Response update(KoodistoDto codesDTO) {
        try {
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(koodistoVersio.getVersio()).build();
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
        updateKoodistoDataType.setCodesGroupUri(koodistoDto.getCodesGroupUri());
        updateKoodistoDataType.setVoimassaAlkuPvm(startDate);
        updateKoodistoDataType.setVoimassaLoppuPvm(endDate);
        updateKoodistoDataType.setKoodistoUri(koodistoDto.getKoodistoUri());
        updateKoodistoDataType.setOmistaja(koodistoDto.getOmistaja());
        updateKoodistoDataType.setOrganisaatioOid(koodistoDto.getOrganisaatioOid());
        updateKoodistoDataType.setVersio(koodistoDto.getVersio());
        updateKoodistoDataType.setTila(TilaType.fromValue(koodistoDto.getTila().toString()));
        updateKoodistoDataType.setLockingVersion(koodistoDto.getVersion());
        for (KoodistoMetadata koodistoMetadata : koodistoDto.getMetadata()) {
            updateKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata, KoodistoMetadataType.class));
        }

        return updateKoodistoDataType;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response insert(KoodistoDto codesDTO) {
        List<String> codesGroupUris = new ArrayList();
        codesGroupUris.add(codesDTO.getCodesGroupUri());
        try {
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, convertFromDTOToCreateKoodistoDataType(codesDTO));
            return Response.status(Response.Status.CREATED).entity(conversionService.convert(koodistoVersio, KoodistoDto.class)).build();
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
            createKoodistoDataType.getMetadataList().add(conversionService.convert(koodistoMetadata, KoodistoMetadataType.class));
        }

        return createKoodistoDataType;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Simple.class)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public List<KoodistoRyhmaListDto> listAllCodesGroups() {
        return conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public List<KoodistoVersioListDto> listAllCodesInAllCodeGroups() {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
        return conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchType), KoodistoVersioListDto.class);
    }

    @GET
    @Path("{codesUri}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public KoodistoListDto getCodesByCodesUri(@PathParam("codesUri") String codesUri) {
        Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);

        return conversionService.convert(koodisto, KoodistoListDto.class);
    }

    @GET
    @Path("{codesUri}/{codesVersion}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public KoodistoDto getCodesByCodesUriAndVersion(@PathParam("codesUri") String codesUri, @PathParam("codesVersion") int codesVersion) {
        KoodistoVersio koodistoVersio = null;
        if (codesVersion == 0) {
            koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(codesUri);
        } else {
            koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri, codesVersion);
        }

        return conversionService.convert(koodistoVersio, KoodistoDto.class);
    }

    @POST
    @Path("upload/{codesUri}")
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("uploadedFile") InputStream fileInputStream,
            @FormDataParam("uploadedFile") com.sun.jersey.core.header.FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("fileFormat") String fileFormat, @FormDataParam("fileEncoding") String fileEncoding, @PathParam("codesUri") String codesUri) {

        String filePath = contentDispositionHeader.getFileName();

        try {
            String mime = "";
            ExportImportFormatType formatStr = null;

            String encoding = fileEncoding;
            if (StringUtils.isBlank(encoding) || !Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }

            if (Format.valueOf(fileFormat) == Format.CSV) {
                mime = "application/octet-stream; charset=" + encoding;
                formatStr = ExportImportFormatType.CSV;
            } else if (Format.valueOf(fileFormat) == Format.JHS_XML) {
                formatStr = ExportImportFormatType.JHS_XML;
                mime = "application/xml";
            } else if (Format.valueOf(fileFormat) == Format.XLS) {
                formatStr = ExportImportFormatType.XLS;
                mime = "application/vnd.ms-excel";
            }
            DataSource ds = new InputStreamDataSource(fileInputStream, mime);
            DataHandler handler = new DataHandler(ds);
            uploadService.upload(codesUri, formatStr, encoding, handler);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu vietyä. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @POST
    @Path("download/{codesUri}/{codesVersion}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ','ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public FileDto download(@PathParam("codesUri") String codesUri, @PathParam("codesVersion") int codesVersion, FileFormatDto fileFormatDto) {
        try {
            ExportImportFormatType formatStr = null;

            Format format = Format.valueOf(fileFormatDto.getFormat());

            if (format == Format.CSV) {
                formatStr = ExportImportFormatType.CSV;
            } else if (format == Format.JHS_XML) {
                formatStr = ExportImportFormatType.JHS_XML;
            } else if (format == Format.XLS) {
                formatStr = ExportImportFormatType.XLS;
            }
            String encoding = fileFormatDto.getEncoding();
            if (StringUtils.isBlank(encoding) || !Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }
            DataHandler handler = downloadService.download(codesUri, codesVersion, formatStr, encoding);
            InputStream inputStream = handler.getInputStream();

            String theString = "";
            if (format == Format.CSV || format == Format.JHS_XML) {
                theString = IOUtils.toString(inputStream, encoding);
            } else { // Binääridataa
                byte[] val = IOUtils.toByteArray(inputStream);
                theString = new String(Base64.encodeBase64(val, false));
            }
            FileDto fileDto = new FileDto();
            fileDto.setData(theString);
            return fileDto;
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu tuotua. ", e);
            return null;
        }
    }

    @POST
    @Path("delete/{codesUri}/{codesVersion}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView({JsonViews.Simple.class})
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response delete(@PathParam("codesUri") String codesUri, @PathParam("codesVersion") int codesVersion) {
        try {
            koodistoBusinessService.delete(codesUri,codesVersion);
            return Response.status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            logger.warn("Koodistoa ei saatu poistettua. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
