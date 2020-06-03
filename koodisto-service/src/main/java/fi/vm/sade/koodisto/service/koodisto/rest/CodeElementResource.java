package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(
        value = "/codeelement",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
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

    private final CodeElementValidator codesValidator = new CodeElementValidator();
    private final CodeElementRelationListValidator relationValidator = new CodeElementRelationListValidator();
    private final ExtendedCodeElementValidator extendedValidator = new ExtendedCodeElementValidator();

    @GetMapping("/{codeElementUri}")
    @JsonView({ JsonViews.Simple.class })
    @ApiOperation(
            value = "Palauttaa koodiversiot tietystä koodista",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public List<SimpleKoodiDto> getAllCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            return conversionService.convertAll(codeElements, SimpleKoodiDto.class);

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementVersionsByCodeElementUri. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement versions by uri failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/{codeElementUri}/{codeElementVersion}")
    @JsonView({ JsonViews.Extended.class })
    @Transactional(readOnly = true)
    @ApiOperation(
            value = "Palauttaa tietyn koodiversion",
            notes = "sisältää koodiversion koodinsuhteet",
            response = ExtendedKoodiDto.class)
    public ExtendedKoodiDto getCodeElementByUriAndVersion(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathVariable("codeElementVersion") int codeElementVersion) {
        try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeElementUri, codeElementVersion);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);

            if (codeElements.size() == 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found");
            }
            return conversionService.convert(codeElements.get(0), ExtendedKoodiDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByUriAndVersion. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri and version failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/{codesUri}/{codesVersion}/{codeElementUri}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = KoodiDto.class)
    public KoodiDto getCodeElementByCodeElementUri(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathVariable("codesVersion") int codesVersion,
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codesuri", "codesversion", "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, codeElementUri);
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codesversion"));

            KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
            return conversionService.convert(codeElement, KoodiDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByCodeElementUri. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/codes/{codesUri}/{codesVersion}")
    @JsonView({ JsonViews.Simple.class })
    @ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")
    public List<SimpleKoodiDto> getAllCodeElementsByCodesUriAndVersion(
            @ApiParam(value = "Koodisto URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodiston versio") @PathVariable("codesVersion") int codesVersion) {
        try {
            String[] errors = { "codesuri", "codesversion" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            ValidatorUtil.checkForGreaterThan(codesVersion, -1, new KoodistoValidationException("error.validation.codeelementversion"));

            List<KoodiVersioWithKoodistoItem> codeElements;
            if (codesVersion == 0) {
                // FIXME: Why return anything when version is invalid?
                codeElements = koodiBusinessService.getKoodisByKoodisto(codesUri, false);
            } else {
                codeElements = koodiBusinessService.getKoodisByKoodistoVersio(codesUri, codesVersion, false);
            }
            return conversionService.convertAll(codeElements, SimpleKoodiDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementsByCodesUriAndVersion. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @GetMapping("/latest/{codeElementUri}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa uusimman koodiversion",
            notes = "",
            response = KoodiDto.class)
    public KoodiDto getLatestCodeElementVersionsByCodeElementUri(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri) {
        try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            if (codeElements.size() < 1) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found");
            }
            return conversionService.convert(codeElements.get(0), KoodiDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getLatestCodeElementVersionsByCodeElementUri. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
    
    @GetMapping("/changes/{codeElementUri}/{codeElementVersion}")
    @JsonView({ JsonViews.Extended.class })
    @ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodiversioon",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)
    public KoodiChangesDto getChangesToCodeElement(@ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathVariable("codeElementVersion") Integer codeElementVersion, 
            @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon", defaultValue = "false") @RequestParam(value= "compareToLatestAccepted", defaultValue = "false") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            return changesService.getChangesDto(codeElementUri, codeElementVersion, compareToLatestAccepted);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
    
    @GetMapping("/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}")
    @JsonView({ JsonViews.Extended.class })
    @ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodiversioon käyttäen lähintä päivämäärään osuvaa koodiversiota vertailussa",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)
    public KoodiChangesDto getChangesToCodeElementWithDate(@ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Kuukauden päivä") @PathVariable("dayofmonth") Integer dayOfMonth,
            @ApiParam(value = "Kuukausi") @PathVariable("month") Integer month,
            @ApiParam(value = "Vuosi") @PathVariable("year") Integer year,
            @ApiParam(value = "Tunti") @PathVariable("hour") Integer hourOfDay,
            @ApiParam(value = "Minuutti") @PathVariable("minute") Integer minute,
            @ApiParam(value = "Sekunti") @PathVariable("second") Integer second,
            @ApiParam(value = "Verrataanko viimeiseen hyväksyttyyn versioon", defaultValue = "false") @RequestParam(value = "compareToLatestAccepted", defaultValue = "false") boolean compareToLatestAccepted) {
        try {
            ValidatorUtil.validateDateParameters(dayOfMonth, month, year, hourOfDay, minute, second);
            DateTime dateTime = new DateTime(year, month, dayOfMonth, hourOfDay, minute, second);
            return changesService.getChangesDto(codeElementUri, dateTime, compareToLatestAccepted);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/{codesUri}")
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää uuden koodin",
            notes = "",
            response = KoodiDto.class)
    public KoodiDto insert(
            @ApiParam(value = "Koodiston URI") @PathVariable("codesUri") String codesUri,
            @ApiParam(value = "Koodi") KoodiDto codeelementDTO) {
        try {
            String[] errors = { "codesuri" };
            ValidatorUtil.validateArgs(errors, codesUri);
            codesValidator.validate(codeelementDTO, ValidationType.INSERT);

            KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                    converter.convertFromDTOToCreateKoodiDataType(codeelementDTO));
            KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter = new KoodiVersioWithKoodistoItemToKoodiDtoConverter();
            koodiVersioWithKoodistoItemToKoodiDtoConverter.setKoodistoConfiguration(koodistoConfiguration);

            return koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: insert. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Inserting codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää relaation koodien välille",
            notes = "")
    public void addRelation(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Linkitettävän koodin URI") @PathVariable("codeElementUriToAdd") String codeElementUriToAdd,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable("relationType") String relationType) {
        try {
            String[] errors = { "codeelementuri", "codeelementuritoadd", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToAdd, relationType);

            koodiBusinessService.addRelation(codeElementUri, Arrays.asList(codeElementUriToAdd), SuhteenTyyppi.valueOf(relationType), false);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelation. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding relation to codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/addrelations")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Lisää koodien välisiä relaatioita, massatoiminto",
            notes = "")
    public void addRelations(
            @ApiParam(value = "Relaation tiedot JSON muodossa") KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.INSERT);

            koodiBusinessService.addRelation(koodiRelaatioDto);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelations. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding multiple relations to codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodien välisen relaation",
            notes = "")
    public void removeRelation(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Irroitettavan koodin URI") @PathVariable("codeElementUriToRemove") String codeElementUriToRemove,
            @ApiParam(value = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable("relationType") String relationType) {

        try {
            String[] errors = { "codeelementuri", "codeelementuritoremove", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToRemove, relationType);

            koodiBusinessService.removeRelation(codeElementUri, Arrays.asList(codeElementUriToRemove),
                    SuhteenTyyppi.valueOf(relationType), false);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelation. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing relation to codeElement failed failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/removerelations")
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(value = "Poistaa koodien välisiä relaatioita, massatoiminto", notes = "")
    public void removeRelations(
            @ApiParam(value = "Relaation tiedot JSON muodossa") KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.UPDATE);

            koodiBusinessService.removeRelation(koodiRelaatioDto);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelations. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing multiple relations form codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/delete/{codeElementUri}/{codeElementVersion}")
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodin",
            notes = "",
            code = org.apache.http.HttpStatus.SC_ACCEPTED)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(
            @ApiParam(value = "Koodin URI") @PathVariable("codeElementUri") String codeElementUri,
            @ApiParam(value = "Koodin versio") @PathVariable("codeElementVersion") int codeElementVersion) {
        try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            koodiBusinessService.delete(codeElementUri, codeElementVersion);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: delete. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Deleting the codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PutMapping
    @JsonView({ JsonViews.Extended.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodin",
            notes = "",
            code = org.apache.http.HttpStatus.SC_CREATED,
            response = KoodiDto.class)
    @ResponseStatus(HttpStatus.CREATED)
    public KoodiDto update(
            @ApiParam(value = "Koodi") KoodiDto codeElementDTO) {
        try {
            codesValidator.validate(codeElementDTO, ValidationType.UPDATE);
            KoodiVersioWithKoodistoItem koodiVersio =
                    koodiBusinessService.updateKoodi(converter.convertFromDTOToUpdateKoodiDataType(codeElementDTO));
            return conversionService.convert(koodiVersio, KoodiDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: update. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Updating codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PutMapping(
            value = "/save",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodin kokonaisuutena",
            notes = "Lisää ja poistaa koodinsuhteita vastaamaan annettua koodia.",
            response = String.class)
    public String save(
            @ApiParam(value = "Koodi") ExtendedKoodiDto koodiDTO) {
        try {
            extendedValidator.validate(koodiDTO, ValidationType.UPDATE);

            KoodiVersio koodiVersio = koodiBusinessService.saveKoodi(koodiDTO);
            return koodiVersio.getVersio().toString();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: save. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Saving codeElement failed.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
}
