package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.validator.*;
import fi.vm.sade.koodisto.validator.Validatable.ValidationType;
import fi.vm.sade.properties.OphProperties;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/rest/codeelement"})
public class CodeElementResource {
    private static final Logger logger = LoggerFactory.getLogger(CodeElementResource.class);

    private static final String KOODIURI = "codeelementuri";
    private static final String KOODIVERSIO = "codeelementversion";
    private static final String KOODISTOURI = "codesuri";
    private static final String KOODISTOVERSIO = "codesversion";
    private static final String RELATIONTYPE = "relationtype";

    private static final String GENERIC_ERROR_CODE = "error.codes.generic";
    private static final String KOODISTO_VALIDATION_ERROR_CODE = "error.validation.codeelementversion";

    final
    KoodiBusinessService koodiBusinessService;

    final
    KoodistoConversionService conversionService;

    final
    KoodiChangesService changesService;
    
    final
    CodeElementResourceConverter converter;

    final
    OphProperties ophProperties;

    private final CodeElementValidator codesValidator = new CodeElementValidator();
    private final CodeElementRelationListValidator relationValidator = new CodeElementRelationListValidator();
    private final ExtendedCodeElementValidator extendedValidator = new ExtendedCodeElementValidator();

    public CodeElementResource(KoodiBusinessService koodiBusinessService, KoodistoConversionService conversionService, KoodiChangesService changesService, CodeElementResourceConverter converter, OphProperties ophProperties) {
        this.koodiBusinessService = koodiBusinessService;
        this.conversionService = conversionService;
        this.changesService = changesService;
        this.converter = converter;
        this.ophProperties = ophProperties;
    }

    @JsonView({ JsonViews.Simple.class }) // tarvitaanko?
    /*@ApiOperation(
            value = "Palauttaa koodiversiot tietystä koodista",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")*/
    @GetMapping(path = "/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllCodeElementVersionsByCodeElementUri(
            @PathVariable String codeElementUri) {
        try {
            String[] errors = { KOODIURI };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            return ResponseEntity.ok(conversionService.convertAll(codeElements, SimpleKoodiDto.class));

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementVersionsByCodeElementUri. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching codeElement versions by uri failed.", e);
            return ResponseEntity.internalServerError().body(message);

        }
    }

    @GetMapping(path = "/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({ JsonViews.Extended.class })
    @Transactional(readOnly = true)
   /* @ApiOperation(
            value = "Palauttaa tietyn koodiversion",
            notes = "sisältää koodiversion koodinsuhteet",
            response = ExtendedKoodiDto.class)*/
    public ResponseEntity<Object> getCodeElementByUriAndVersion(
            @PathVariable String codeElementUri,
            @PathVariable int codeElementVersion) {
        try {
            String[] errors = { KOODIURI, KOODIVERSIO };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException(KOODISTO_VALIDATION_ERROR_CODE));

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeElementUri, codeElementVersion);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);

            if (codeElements.size() == 0) {
                return ResponseEntity.internalServerError().body("error.codeelement.not.found");
            }
            return ResponseEntity.ok(conversionService.convert(codeElements.get(0), ExtendedKoodiDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByUriAndVersion. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching codeElement by uri and version failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @GetMapping(path = "/{codesUri}/{codesVersion}/{codeElementUri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = KoodiDto.class)*/
    public ResponseEntity<Object> getCodeElementByCodeElementUri(
            @PathVariable String codesUri,
            @PathVariable int codesVersion,
            @PathVariable String codeElementUri) {
        try {
            String[] errors = { KOODISTOURI, KOODISTOVERSIO, KOODIURI };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, codeElementUri);
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codesversion"));

            KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
            return ResponseEntity.ok(conversionService.convert(codeElement, KoodiDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByCodeElementUri. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching codeElement by uri failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Simple.class })
    /*@ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")*/
    @GetMapping(path = "/codes/{codesUri}/{codesVersion}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Object> getAllCodeElementsByCodesUriAndVersion(
            @PathVariable String codesUri,
            @PathVariable int codesVersion) {
        try {
            String[] errors = { KOODISTOURI, KOODISTOVERSIO };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            ValidatorUtil.checkForGreaterThan(codesVersion, -1, new KoodistoValidationException(KOODISTO_VALIDATION_ERROR_CODE));

            List<KoodiVersioWithKoodistoItem> codeElements = null;
            if (codesVersion == 0) {
                // FIXME: Why return anything when version is invalid?
                codeElements = koodiBusinessService.getKoodisByKoodisto(codesUri, false);
            } else {
                codeElements = koodiBusinessService.getKoodisByKoodistoVersio(codesUri, codesVersion, false);
            }
            return ResponseEntity.ok(conversionService.convertAll(codeElements, SimpleKoodiDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementsByCodesUriAndVersion. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /*@ApiOperation(
            value = "Palauttaa uusimman koodiversion",
            notes = "",
            response = KoodiDto.class)*/
    @JsonView({ JsonViews.Basic.class })
    @GetMapping(path = "/latest/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLatestCodeElementVersionsByCodeElementUri(
            @PathVariable String codeElementUri) {
        try {
            String[] errors = { KOODIURI };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            if (codeElements.size() < 1) {
                return ResponseEntity.internalServerError().body("error.codeelement.not.found");
            }
            return ResponseEntity.ok(conversionService.convert(codeElements.get(0), KoodiDto.class));

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getLatestCodeElementVersionsByCodeElementUri. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching codeElement by uri failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodiversioon",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)*/
    @GetMapping(path = "/changes/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodeElement(@PathVariable String codeElementUri,
            @PathVariable Integer codeElementVersion,
            @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException(KOODISTO_VALIDATION_ERROR_CODE));
            return ResponseEntity.ok(changesService.getChangesDto(codeElementUri, codeElementVersion, compareToLatestAccepted));

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching changes to code element failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodiversioon käyttäen lähintä päivämäärään osuvaa koodiversiota vertailussa",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)*/
    @GetMapping(path = "/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodeElementWithDate(@PathVariable String codeElementUri,
                                                           @PathVariable Integer dayofmonth,
                                                           @PathVariable Integer month,
                                                           @PathVariable Integer year,
                                                           @PathVariable Integer hour,
                                                           @PathVariable Integer minute,
                                                           @PathVariable Integer second,
                                                           @RequestParam(defaultValue = "false") Boolean compareToLatestAccepted) {
         try {
            ValidatorUtil.validateDateParameters(dayofmonth, month, year, hour, minute, second);
            DateTime dateTime = new DateTime(year, month, dayofmonth, hour, minute, second);
            return ResponseEntity.ok(changesService.getChangesDto(codeElementUri, dateTime, compareToLatestAccepted));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Fetching changes to code element failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Lisää uuden koodin",
            notes = "",
            response = Response.class)*/
    @PostMapping(path = "/{codesUri}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public ResponseEntity<Object> insert(
            @PathVariable String codesUri,
            @RequestBody KoodiDto codeelementDTO) {
        try {
            String[] errors = { KOODISTOURI };
            ValidatorUtil.validateArgs(errors, codesUri);
            codesValidator.validate(codeelementDTO, ValidationType.INSERT);
            // aika huono toteutus
            KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                    converter.convertFromDTOToCreateKoodiDataType(codeelementDTO));
            KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter = new KoodiVersioWithKoodistoItemToKoodiDtoConverter(ophProperties);
            return ResponseEntity.status(201).body(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: insert. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Inserting codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Lisää relaation koodien välille",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addRelation(
            @PathVariable String codeElementUri,
            @PathVariable String codeElementUriToAdd,
            @PathVariable String relationType) {
        try {
            String[] errors = { KOODIURI, "codeelementuritoadd", RELATIONTYPE };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToAdd, relationType);

            koodiBusinessService.addRelation(codeElementUri, List.of(codeElementUriToAdd), SuhteenTyyppi.valueOf(relationType), false);
            return ResponseEntity.ok(null);

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelation. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Adding relation to codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Lisää koodien välisiä relaatioita, massatoiminto",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/addrelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addRelations(
            @RequestBody KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.INSERT);

            koodiBusinessService.addRelation(koodiRelaatioDto);
            return ResponseEntity.ok(null);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelations. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Adding multiple relations to codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Poistaa koodien välisen relaation",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeRelation(
            @PathVariable String codeElementUri,
            @PathVariable String codeElementUriToRemove,
            @PathVariable String relationType) {

         try {
            String[] errors = { KOODIURI, "codeelementuritoremove", RELATIONTYPE };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToRemove, relationType);

            koodiBusinessService.removeRelation(codeElementUri, List.of(codeElementUriToRemove),
                    SuhteenTyyppi.valueOf(relationType), false);
            return ResponseEntity.ok(null);
        } catch (KoodistoValidationException e) {
             logger.warn("Invalid parameter for rest call: removeRelation. {}", e.getMessage());
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (SadeBusinessException e) {
             String message = e.getMessage();
             logger.error("Removing relation to codeElement failed with SadeBusinessException. {}", message);
             return ResponseEntity.internalServerError().body(message);
         } catch (Exception e) {
             String message = GENERIC_ERROR_CODE;
             logger.error("Removing relation to codeElement failed with generic exception. {}", message);
             return ResponseEntity.internalServerError().body(message);
         }
    }

    @JsonView({ JsonViews.Extended.class })
    //@ApiOperation(value = "Poistaa koodien välisiä relaatioita, massatoiminto", notes = "")
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/removerelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeRelations(
            @RequestBody KoodiRelaatioListaDto koodiRelaatioDto
            ) {
         try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.UPDATE);
            koodiBusinessService.removeRelation(koodiRelaatioDto);
            return ResponseEntity.ok(null);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelations. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Removing multiple relations form codeElement failed. {}", message);
            return ResponseEntity.internalServerError().body(message);

        }
    }

    // pitääis olla delete method
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Poistaa koodin",
            notes = "",
            response = Response.class)*/
    @PostMapping(path = "/delete/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(
            @PathVariable String codeElementUri,
            @PathVariable int codeElementVersion) {
         try {
            String[] errors = { KOODIURI, KOODIVERSIO };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException(KOODISTO_VALIDATION_ERROR_CODE));

            koodiBusinessService.delete(codeElementUri, codeElementVersion);
            return ResponseEntity.status(202).body(null);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: delete. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Deleting the codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
         }
    }

    @JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Päivittää koodin",
            notes = "",
            response = Response.class)*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @RequestBody KoodiDto codeElementDTO) {
        try {
            codesValidator.validate(codeElementDTO, ValidationType.UPDATE);
            KoodiVersioWithKoodistoItem koodiVersio =
                    koodiBusinessService.updateKoodi(converter.convertFromDTOToUpdateKoodiDataType(codeElementDTO));
            return ResponseEntity.status(201).body(conversionService.convert(koodiVersio, KoodiDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: update. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Updating codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Päivittää koodin kokonaisuutena",
            notes = "Lisää ja poistaa koodinsuhteita vastaamaan annettua koodia.",
            response = Response.class)*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PutMapping(path = "/save", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(
            @RequestBody ExtendedKoodiDto koodiDTO) {
        try {
            extendedValidator.validate(koodiDTO, ValidationType.UPDATE);

            KoodiVersio koodiVersio = koodiBusinessService.saveKoodi(koodiDTO);
            return ResponseEntity.ok(koodiVersio.getVersio().toString());
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: save. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            logger.error("Saving codeElement failed.", e);
            return ResponseEntity.internalServerError().body(message);
        }
    }
}
