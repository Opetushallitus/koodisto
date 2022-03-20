package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.validator.CodesValidator;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/rest/codes"})
public class CodesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodesResource.class);

    private static final String KOODISTOURI = "codesuri";
    private static final String KOODISTOVERSIO = "codesversion";
    private static final String RELATIONTYPE = "relationtype";

    private static final String GENERIC_ERROR_CODE = "error.codes.generic";

    private final KoodistoBusinessService koodistoBusinessService;

    private final KoodistoConversionService conversionService;


    private final CodesResourceConverter converter;

    private final KoodistoChangesService changesService;

    private final CodesValidator codesValidator = new CodesValidator();

    public CodesResource(KoodistoBusinessService koodistoBusinessService, KoodistoConversionService conversionService, CodesResourceConverter converter, KoodistoChangesService changesService) {
        this.koodistoBusinessService = koodistoBusinessService;
        this.conversionService = conversionService;
        this.converter = converter;
        this.changesService = changesService;
    }

    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @JsonView({ JsonViews.Extended.class })
    @PostMapping(path = "/addrelation/{codesUri}/{codesUriToAdd}/{relationType}", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    /*@ApiOperation(
            value = "Lisää relaatio koodistojen välille",
            notes = "")*/
    public ResponseEntity<?> addRelation(
            @PathVariable String codesUri,
            @PathVariable String codesUriToAdd,
            @PathVariable String relationType) {
       try {
            String[] errors = { KOODISTOURI, "codesuritoadd", RELATIONTYPE };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToAdd, relationType);

            koodistoBusinessService.addRelation(codesUri, codesUriToAdd, SuhteenTyyppi.valueOf(relationType));
            return ResponseEntity.ok(null);

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: addRelation. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Adding relation to codes failed. ", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @JsonView({ JsonViews.Extended.class })
    @PostMapping(path = "/removerelation/{codesUri}/{codesUriToRemove}/{relationType}", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    /* DELETE kuuluisi olla
        @ApiOperation(
            value = "Poistaa relaatio koodistojen väliltä",
            notes = "")*/
    public ResponseEntity<?> removeRelation(
            @PathVariable String codesUri,
            @PathVariable String codesUriToRemove,
            @PathVariable String relationType) {

         try {
            String[] errors = { KOODISTOURI, "codesuritoremove", RELATIONTYPE };
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToRemove, relationType);

            koodistoBusinessService.removeRelation(codesUri, List.of(codesUriToRemove), SuhteenTyyppi.valueOf(relationType));
            return ResponseEntity.ok(null);

         } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: removeRelation. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Removing relation from codes failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }


    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @JsonView({ JsonViews.Basic.class })
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    /*@ApiOperation(
            value = "Päivittää koodistoa",
            notes = "",
            response = Response.class)*/
    public ResponseEntity<?> update(
            @RequestBody KoodistoDto codesDTO) {
         try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(converter.convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return ResponseEntity.status(201).body(koodistoVersio.getVersio());

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: update. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Updating codes failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Päivittää koodiston kokonaisuutena",
            notes = "Lisää ja poistaa koodistonsuhteita",
            response = Response.class)*/
    @JsonView({ JsonViews.Basic.class })
    @PutMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(
            @RequestBody KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);

            KoodistoVersio koodistoVersio = koodistoBusinessService.saveKoodisto(codesDTO);
            return ResponseEntity.ok(koodistoVersio.getVersio().toString());

         } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: save. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Saving codes failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @JsonView({ JsonViews.Basic.class })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    /* @ApiOperation(
            value = "Lisää koodiston",
            notes = "",
            response = Response.class)*/
    public ResponseEntity<?> insert(
            @RequestBody KoodistoDto codesDTO) {
        try {
            codesValidator.validate(codesDTO, ValidationType.INSERT);
            List<String> codesGroupUris = new ArrayList<>();
            codesGroupUris.add(codesDTO.getCodesGroupUri());
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, converter.convertFromDTOToCreateKoodistoDataType(codesDTO));
            return ResponseEntity.status(201).body(conversionService.convert(koodistoVersio, KoodistoDto.class));
         } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: insert. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Inserting codes failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }


    /*@ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät",
            notes = "",
            response = KoodistoRyhmaListDto.class,
            responseContainer = "List")*/
    @JsonView(JsonViews.Simple.class)
    @GetMapping(path = "", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> listAllCodesGroups() {
       try {
            return ResponseEntity.ok(conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class));

         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Listing all codes groups failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /*@ApiOperation(
            value = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot",
            notes = "",
            response = KoodistoVersioListDto.class,
            responseContainer = "List")*/
    @JsonView({ JsonViews.Basic.class })
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listAllCodesInAllCodeGroups() {
         try {

            SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
            return ResponseEntity.ok(conversionService.convertAll(koodistoBusinessService.searchKoodistos(searchType), KoodistoVersioListDto.class));

         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Listing all codes in all codes groups failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

   /* @ApiOperation(
            value = "Palauttaa koodiston",
            notes = "",
            response = KoodistoListDto.class)*/
   @JsonView({ JsonViews.Basic.class })
   @GetMapping(path = "/{codesUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCodesByCodesUri(
            @PathVariable String codesUri) {
        try {
            String[] errors = { KOODISTOURI };
            ValidatorUtil.validateArgs(errors, codesUri);
            Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);
            return ResponseEntity.ok(conversionService.convert(koodisto, KoodistoListDto.class));

        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUri. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Getting codes by codes uri failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /*
    @ApiOperation(
            value = "Palauttaa tietyn koodistoversion",
            notes = "",
            response = KoodistoDto.class)*/
    @JsonView({ JsonViews.Extended.class })
    @GetMapping(path = "/{codesUri}/{codesVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCodesByCodesUriAndVersion(
            @PathVariable String codesUri,
            @PathVariable int codesVersion) {
         try {
            String[] errors = { KOODISTOURI, KOODISTOVERSIO };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);

            KoodistoVersio koodistoVersio;
            if (codesVersion == 0) {
                koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(codesUri);
            } else {
                koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri, codesVersion);
            }

            return ResponseEntity.ok(conversionService.convert(koodistoVersio, KoodistoDto.class));

          } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: getCodesByCodesUriAndVersion. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Getting codes by codes uri and version failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /*@ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodistoversioon verrattaessa",
            notes = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodistoChangesDto.class)*/
    @JsonView({ JsonViews.Basic.class })
    @GetMapping(path = "/changes/{codesUri}/{codesVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getChangesToCodes(@PathVariable String codesUri,
                                      @PathVariable Integer codesVersion,
            @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) { // queryparam
        try {
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            return ResponseEntity.ok(changesService.getChangesDto(codesUri, codesVersion, compareToLatestAccepted));
        } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: get changes. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Fetching changes to codes failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }


   /* @ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodistoversioon käyttäen lähintä päivämäärään osuvaa koodistoversiota vertailussa",
            notes = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodistoChangesDto.class)*/
   @JsonView({ JsonViews.Basic.class })
   @GetMapping(path = "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getChangesToCodesWithDate(@PathVariable String codesUri,
                                              @PathVariable Integer dayofmonth,
                                              @PathVariable Integer month,
                                              @PathVariable Integer year,
                                              @PathVariable Integer hour,
                                              @PathVariable Integer minute,
                                              @PathVariable Integer second,
            @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) {
         try {
            ValidatorUtil.validateDateParameters(dayofmonth, month, year, hour, minute, second);
            DateTime date = new DateTime(year, month, dayofmonth, hour, minute, second);
            return ResponseEntity.ok(changesService.getChangesDto(codesUri, date, compareToLatestAccepted));
         } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: get changes. ", e);
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Fetching changes to codes failed.", e);
             return ResponseEntity.internalServerError().body(message);
         }
    }

    /* pitäs olla delete
    @ApiOperation(
            value = "Poistaa koodiston",
            notes = "",
            response = Response.class) */
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @JsonView({ JsonViews.Simple.class })
    @PostMapping(path = "/delete/{codesUri}/{codesVersion}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(
            @PathVariable String codesUri,
            @PathVariable int codesVersion) {
         try {
            String[] errors = { KOODISTOURI, KOODISTOVERSIO };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);

            koodistoBusinessService.delete(codesUri, codesVersion);
            return ResponseEntity.status(202).body(null);
         } catch (KoodistoValidationException e) {
            LOGGER.warn("Invalid parameter for rest call: delete. ", e);
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            LOGGER.error("Deleting codes failed.", e);
             return ResponseEntity.internalServerError().body(message);
         }
    }
}
