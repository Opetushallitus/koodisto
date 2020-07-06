package fi.vm.sade.koodisto.service.koodisto.rest;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.DownloadBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.UploadBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodesValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(
        value = "/codes",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
@Api(value = "/rest/codes", description = "Koodistot")
public class CodesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesResource.class);

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private UploadBusinessService uploadService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private DownloadBusinessService downloadBusinessService;

    @Autowired
    private CodesResourceConverter converter;

    @Autowired
    private KoodistoChangesService changesService;

    private CodesValidator codesValidator = new CodesValidator();

    @PostMapping("/addrelation/{codesUri}/{codesUriToAdd}/{relationType}")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää relaatio koodistojen välille",
            notes = "",
            code = org.apache.http.HttpStatus.SC_CREATED)
    @ResponseStatus(HttpStatus.CREATED)
    public void addRelation(
            @ApiParam(value = "Koodiston URI") @RequestParam("codesUri") String codesUri,
            @ApiParam(value = "Linkitettävän koodiston URI") @RequestParam("codesUriToAdd") String codesUriToAdd,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @RequestParam("relationType") String relationType) {
        try {
            String[] errors = { "codesuri", "codesuritoadd", "relationtype" };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToAdd, relationType);

            koodistoBusinessService.addRelation(codesUri, codesUriToAdd, SuhteenTyyppi.valueOf(relationType));

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: addRelation. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Adding relation to codes failed. ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/removerelation/{codesUri}/{codesUriToRemove}/{relationType}")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa relaatio koodistojen väliltä",
            notes = "",
            code = org.apache.http.HttpStatus.SC_CREATED)
    @ResponseStatus(HttpStatus.CREATED)
    public void removeRelation(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Irrotettavan koodiston URI") @PathVariable("codesUriToRemove") String codesUriToRemove,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable("relationType") String relationType) {

        try {
            String[] errors = { "codesUri", "codesuritoremove", "relationtype" };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToRemove, relationType);

            koodistoBusinessService.removeRelation(codesUri, Arrays.asList(codesUriToRemove), SuhteenTyyppi.valueOf(relationType));
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: removeRelation. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Removing relation from codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PutMapping()
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodistoa",
            notes = "",
            response = Integer.class,
            code = org.apache.http.HttpStatus.SC_CREATED)
    @ResponseStatus(HttpStatus.CREATED)
    public Integer update(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(converter.convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return koodistoVersio.getVersio();

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: update. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Updating codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PutMapping("/save")
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodiston kokonaisuutena",
            notes = "Lisää ja poistaa koodistonsuhteita",
            response = String.class)
    public String save(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);

            KoodistoVersio koodistoVersio = koodistoBusinessService.saveKoodisto(codesDTO);
            return koodistoVersio.getVersio().toString(); // TODO: WTF? update -> integer, save -> string???
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: save. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Saving codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping()
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää koodiston",
            notes = "",
            response = KoodistoDto.class,
            code = org.apache.http.HttpStatus.SC_CREATED)
    @ResponseStatus(HttpStatus.CREATED)
    public KoodistoDto insert(
            @ApiParam(value = "Koodisto") KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.INSERT);
            List<String> codesGroupUris = new ArrayList<>();
            codesGroupUris.add(codesDTO.getCodesGroupUri());
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, converter.convertFromDTOToCreateKoodistoDataType(codesDTO));
            return conversionService.convert(koodistoVersio, KoodistoDto.class);

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: insert. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Inserting codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping()
    @JsonView(JsonViews.Simple.class)
    @ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät",
            notes = "",
            response = KoodistoRyhmaListDto.class,
            responseContainer = "List")
    public List<KoodistoRyhmaListDto> listAllCodesGroups() {
        try {
            return conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Listing all codes groups failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/all")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot",
            notes = "",
            response = KoodistoVersioListDto.class,
            responseContainer = "List")
    public List<KoodistoVersioListDto> listAllCodesInAllCodeGroups() {
        try {

            SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
            return conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchType), KoodistoVersioListDto.class);

        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Listing all codes in all codes groups failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/{codesUri}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodiston",
            notes = "",
            response = KoodistoListDto.class)
    public KoodistoListDto getCodesByCodesUri(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri) {
        try {
            String[] errors = { "codesuri" };
            ValidatorUtil.validateArgs(errors, codesUri);

            Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);

            return conversionService.convert(koodisto, KoodistoListDto.class);

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUri. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Getting codes by codes uri failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/{codesUri}/{codesVersion}")
    @JsonView({ JsonViews.Extended.class })
    @ApiOperation(
            value = "Palauttaa tietyn koodistoversion",
            notes = "",
            response = KoodistoDto.class)
    public KoodistoDto getCodesByCodesUriAndVersion(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodiston vesio") @PathVariable("codesVersion") int codesVersion) {
        try {
            String[] errors = { "codesuri", "codesversion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);

            KoodistoVersio koodistoVersio = null;
            if (codesVersion == 0) {
                koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(codesUri);
            } else {
                koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri, codesVersion);
            }

            return conversionService.convert(koodistoVersio, KoodistoDto.class);

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUriAndVersion. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Getting codes by codes uri and version failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/changes/{codesUri}/{codesVersion}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodistoversioon verrattaessa",
            notes = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodistoChangesDto.class)
    public KoodistoChangesDto getChangesToCodes(@ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
                                                @ApiParam(value = "Koodiston versio") @PathVariable("codesVersion") Integer codesVersion,
                                                @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon", defaultValue = "false") @PathVariable(value = "compareToLatestAccepted", required = false) boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            return changesService.getChangesDto(codesUri, codesVersion, compareToLatestAccepted);
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: get changes. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Fetching changes to codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodistoversioon käyttäen lähintä päivämäärään osuvaa koodistoversiota vertailussa",
            notes = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodistoChangesDto.class)
    public KoodistoChangesDto getChangesToCodesWithDate(@ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
                                                        @ApiParam(value = "Kuukauden päivä") @PathVariable("dayofmonth") Integer dayOfMonth,
                                                        @ApiParam(value = "Kuukausi") @PathVariable("month") Integer month,
                                                        @ApiParam(value = "Vuosi") @PathVariable("year") Integer year,
                                                        @ApiParam(value = "Tunti") @PathVariable("hour") Integer hourOfDay,
                                                        @ApiParam(value = "Minuutti") @PathVariable("minute") Integer minute,
                                                        @ApiParam(value = "Sekunti") @PathVariable("second") Integer second,
                                                        @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon", defaultValue = "false") @RequestParam(value = "compareToLatestAccepted", defaultValue = "false") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.validateDateParameters(dayOfMonth, month, year, hourOfDay, minute, second);
            DateTime date = new DateTime(year, month, dayOfMonth, hourOfDay, minute, second);
            return changesService.getChangesDto(codesUri, date, compareToLatestAccepted);
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: get changes. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Fetching changes to codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping(
            value = "/upload/{codesUri}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Tuo koodiston tiedostosta",
            notes = "",
            response = String.class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFile(
            @ApiParam(value = "Tuotava tiedosto") @RequestParam(value = "uploadedFile", required = false) MultipartFile uploadedFile,
            @ApiParam(value = "Tiedostotyyppi") @RequestParam(value = "fileFormat", required = false) String fileFormat,
            @ApiParam(value = "Tiedoston koodaus") @RequestParam(value = "fileEncoding", required = false) String fileEncoding,
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri) {
        try {
            String[] errors = { "file", "fileformat", "codesuri" };
            ValidatorUtil.validateArgs(errors, uploadedFile, fileFormat, codesUri);
            if (StringUtils.isBlank(fileEncoding) && !fileFormat.equals("XLS")) {
                // Encoding can be empty if filetype is binary XLS
                throw new KoodistoValidationException("error.validation.fileencoding");
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

            /*DataSource ds = new ByteArrayData;
            DataHandler handler = new DataHandler(ds);
            KoodistoVersio kv = uploadService.upload(codesUri, formatStr, encoding, handler);
            return kv.getVersio().toString();*/
            return null;

            // IE9 ei osaa käsitellä iframeja nätisti, jos palvelimelta tulee 500. ngUpload direktiivi kaatuu Access Denied virheeseen. Siksi tallennus
            // palauttaa myös virhetilanteissa 200.
            // TODO: BURN IT WITH FIRE!!!
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: uploadFile. ", e);
            return e.getMessage();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Uploading codes failed.", e);
            return message;
        }

    }

    @GetMapping(
            value = "/download/{codesUri}/{codesVersion}/{fileFormat}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Lataa koodiston CSV, XML tai XLS tiedostona.",
            notes = "Palauttaa tyhjän koodistopohjan, jos koodiston URI on 'blankKoodistoDocument' ja versio on -1.",
            response = ResponseEntity.class)
    public ResponseEntity<File> download(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathVariable("codesVersion") int codesVersion,
            @ApiParam(value = "Tiedostotyyppi (JHS_XML, CSV, XLS)") @PathVariable("fileFormat") Format fileFormat,
            @ApiParam(value = "Tiedoston merkistö (UTF-8, ISO-88519-1, ISO-88519-15)", defaultValue = "UTF-8") @RequestParam(value = "encoding", defaultValue = "UTF-8") String encoding) {
        try {
            String[] errors = { "codesuri", "codesversion", "fileformat", "encoding" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, fileFormat, encoding);

            File file = koodistoBusinessService.downloadFile(codesUri, codesVersion, fileFormat, encoding);
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
            String contentDisposition = "inline; filename=\"" + codesUri + extension + "\"";
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentLength(file.length())
                    .body(file);

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: downloadFile. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Downloading codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/delete/{codesUri}/{codesVersion}")
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodiston",
            notes = "",
            code = org.apache.http.HttpStatus.SC_ACCEPTED)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathVariable("codesVersion") int codesVersion) {
        try {
            String[] errors = { "codesuri", "codesversion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);

            koodistoBusinessService.delete(codesUri, codesVersion);
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: delete. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            LOGGER.error("Deleting codes failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
}
