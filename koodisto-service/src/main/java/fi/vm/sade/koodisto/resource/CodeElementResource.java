package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.service.impl.conversion.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.*;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping({"/codeelement"})
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

    // @JsonView({ JsonViews.Simple.class }) // tarvitaanko?
    /*@ApiOperation(
            value = "Palauttaa koodiversiot tietystä koodista",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")*/
    @GetMapping(path = "/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleKoodiDto getAllCodeElementVersionsByCodeElementUri(
            @PathVariable String codeElementUri) {
        try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            return conversionService.convertAll(codeElements, SimpleKoodiDto.class);

        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementVersionsByCodeElementUri. ", e);
            throw new KoodistoValidationException(e);
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement versions by uri failed.", e);
            throw new SadeBusinessException(message);
        }
    }

    @GetMapping(path = "/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@JsonView({ JsonViews.Extended.class })
    @Transactional(readOnly = true)
   /* @ApiOperation(
            value = "Palauttaa tietyn koodiversion",
            notes = "sisältää koodiversion koodinsuhteet",
            response = ExtendedKoodiDto.class)*/
    public ExtendedKoodiDto getCodeElementByUriAndVersion(
            @PathVariable String codeElementUri,
            @PathVariable int codeElementVersion) {
        //try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(codeElementUri, codeElementVersion);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            return conversionService.convert(codeElements.get(0), ExtendedKoodiDto.class);

            /*
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
        */
    }

    @GetMapping(path = "/{codesUri}/{codesVersion}/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = KoodiDto.class)*/
    public Response getCodeElementByCodeElementUri(
            @PathVariable String codesUri,
            @PathVariable int codesVersion,
            @PathVariable String codeElementUri) {
        //try {
            String[] errors = { "codesuri", "codesversion", "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion, codeElementUri);
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codesversion"));

            KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
            return conversionService.convert(codeElement, KoodiDto.class);
        /*
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getCodeElementByCodeElementUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
        */
    }

    //@JsonView({ JsonViews.Simple.class })
    /*@ApiOperation(
            value = "Palauttaa koodin tietystä koodistoversiosta",
            notes = "",
            response = SimpleKoodiDto.class,
            responseContainer = "List")*/
    @GetMapping(path = "/codes/{codesUri}/{codesVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleKoodiDto getAllCodeElementsByCodesUriAndVersion(
            @PathVariable String codesUri,
            @PathVariable int codesVersion) {
       // try {
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
            return conversionService.convertAll(codeElements, SimpleKoodiDto.class);
        /*
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getAllCodeElementsByCodesUriAndVersion. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Basic.class })

    /*@ApiOperation(
            value = "Palauttaa uusimman koodiversion",
            notes = "",
            response = KoodiDto.class)*/
    @GetMapping(path = "/latest/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public KoodiDto getLatestCodeElementVersionsByCodeElementUri(
            @PathVariable String codeElementUri) {
        //try {
            String[] errors = { "codeelementuri" };
            ValidatorUtil.validateArgs(errors, codeElementUri);

            SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
            List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
            if (codeElements.size() < 1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("error.codeelement.not.found").build();
            }
            return conversionService.convert(codeElements.get(0), KoodiDto.class);
        /*
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: getLatestCodeElementVersionsByCodeElementUri. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching codeElement by uri failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Palauttaa muutokset uusimpaan koodiversioon",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)*/
    @GetMapping(path = "/changes/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public KoodiChangesDto getChangesToCodeElement(@ApiParam(value = "Koodin URI") @PathVariable String codeElementUri,
            @PathVariable Integer codeElementVersion,
            @DefaultValue("false") @Parameter() boolean compareToLatestAccepted) {
        //try {
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            return changesService.getChangesDto(codeElementUri, codeElementVersion, compareToLatestAccepted);
    /*
    } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Palauttaa tehdyt muutokset uusimpaan koodiversioon käyttäen lähintä päivämäärään osuvaa koodiversiota vertailussa",
            notes = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.",
            response = KoodiChangesDto.class)*/
    @GetMapping(path = "/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", produces = MediaType.APPLICATION_JSON_VALUE)
    public KoodiChangesDto getChangesToCodeElementWithDate(@PathVariable String codeElementUri,
                                                           @PathVariable Integer dayOfMonth,
                                                           @PathVariable Integer month,
                                                           @PathVariable Integer year,
                                                           @PathVariable Integer hourOfDay,
                                                           @PathVariable nteger minute,
                                                           @PathVariable Integer second,
                                                           @DefaultValue("false") @Parameter("compareToLatestAccepted") boolean compareToLatestAccepted) {
        // try {
            ValidatorUtil.validateDateParameters(dayOfMonth, month, year, hourOfDay, minute, second);
            DateTime dateTime = new DateTime(year, month, dayOfMonth, hourOfDay, minute, second);
            return changesService.getChangesDto(codeElementUri, dateTime, compareToLatestAccepted);
       /* } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: get changes. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Fetching changes to code element failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Lisää uuden koodin",
            notes = "",
            response = Response.class)*/
    @PostMapping(path = "/{codesUri}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    public Response insert(
            @PathVariable String codesUri,
            @Parameter(value = "Koodi") KoodiDto codeelementDTO) {
        //try {
            String[] errors = { "codesuri" };
            ValidatorUtil.validateArgs(errors, codesUri);
            codesValidator.validate(codeelementDTO, ValidationType.INSERT);
            // TODO huono toteutus
            KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                    converter.convertFromDTOToCreateKoodiDataType(codeelementDTO));
            KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter = new KoodiVersioWithKoodistoItemToKoodiDtoConverter();
            koodiVersioWithKoodistoItemToKoodiDtoConverter.setKoodistoConfiguration(koodistoConfiguration);

            return koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem);
       /* } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: insert. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Inserting codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }


    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Lisää relaation koodien välille",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addRelation(
            @PathVariable String codeElementUri,
            @PathVariable String codeElementUriToAdd,
            @PathVariable String relationType) {
      //  try {
            String[] errors = { "codeelementuri", "codeelementuritoadd", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToAdd, relationType);

            koodiBusinessService.addRelation(codeElementUri, Arrays.asList(codeElementUriToAdd), SuhteenTyyppi.valueOf(relationType), false);
            return;  //Response.status(Response.Status.OK).build();

    /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding relation to codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Lisää koodien välisiä relaatioita, massatoiminto",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/addrelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addRelations(
            @RequestBody KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        //try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.INSERT);

            koodiBusinessService.addRelation(koodiRelaatioDto);
            return Response.status(Response.Status.OK).build();
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: addRelations. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Adding multiple relations to codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Poistaa koodien välisen relaation",
            notes = "")*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeRelation(
            @PathVariable String codeElementUri,
            @PathVariable String codeElementUriToRemove,
            @PathVariable String relationType) {

        // try {
            String[] errors = { "codeelementuri", "codeelementuritoremove", "relationtype" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementUriToRemove, relationType);

            koodiBusinessService.removeRelation(codeElementUri, Arrays.asList(codeElementUriToRemove),
                    SuhteenTyyppi.valueOf(relationType), false);
            return;
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelation. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing relation to codeElement failed failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    //@ApiOperation(value = "Poistaa koodien välisiä relaatioita, massatoiminto", notes = "")
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PostMapping(path = "/removerelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeRelations(
            @RequestBody KoodiRelaatioListaDto koodiRelaatioDto
            ) {
        // try {
            relationValidator.validate(koodiRelaatioDto, ValidationType.UPDATE);

            koodiBusinessService.removeRelation(koodiRelaatioDto);
            return;
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: removeRelations. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Removing multiple relations form codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    // TODO oikea http metodi olisi delete
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
    @PostMapping(path = "/delete/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(
            @PathVariable String codeElementUri,
            @PathVariable int codeElementVersion) {
        // try {
            String[] errors = { "codeelementuri", "codeelementversion" };
            ValidatorUtil.validateArgs(errors, codeElementUri, codeElementVersion);
            ValidatorUtil.checkForGreaterThan(codeElementVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));

            koodiBusinessService.delete(codeElementUri, codeElementVersion);
            return;
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: delete. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Deleting the codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Extended.class })
    /*@ApiOperation(
            value = "Päivittää koodin",
            notes = "",
            response = Response.class)*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public KoodiDto update(
            @RequestBody KoodiDto codeElementDTO) {
        //try {
            codesValidator.validate(codeElementDTO, ValidationType.UPDATE);
            KoodiVersioWithKoodistoItem koodiVersio =
                    koodiBusinessService.updateKoodi(converter.convertFromDTOToUpdateKoodiDataType(codeElementDTO));
            return conversionService.convert(koodiVersio, KoodiDto.class);
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: update. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Updating codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }

    //@JsonView({ JsonViews.Basic.class })
    /*@ApiOperation(
            value = "Päivittää koodin kokonaisuutena",
            notes = "Lisää ja poistaa koodinsuhteita vastaamaan annettua koodia.",
            response = Response.class)*/
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @PutMapping(path = "/save", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String save(
            @RequestBody ExtendedKoodiDto koodiDTO) {
        //try {
            extendedValidator.validate(koodiDTO, ValidationType.UPDATE);

            KoodiVersio koodiVersio = koodiBusinessService.saveKoodi(koodiDTO);
            return koodiVersio.getVersio().toString();
        /*} catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call: save. ", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            logger.error("Saving codeElement failed.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }*/
    }
}
