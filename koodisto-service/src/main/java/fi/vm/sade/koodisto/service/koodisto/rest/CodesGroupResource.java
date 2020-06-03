package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.CodesGroupValidator;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.ValidatorUtil;
import fi.vm.sade.koodisto.service.koodisto.rest.validator.Validatable.ValidationType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(
        value = "/codesgroup",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Api(value = "/rest/codesgroup", description = "Koodistoryhmät")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @Autowired
    private KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @Autowired
    private UriTransliterator uriTransliterator;

    private final CodesGroupValidator codesGroupValidator = new CodesGroupValidator();

    @GetMapping("/{id}")
    @JsonView({ JsonViews.Basic.class })
    @ApiOperation(
            value = "Palauttaa koodistoryhmän",
            notes = "",
            response = KoodistoRyhmaDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei löydy kyseisellä id:llä")
    })
    public KoodistoRyhmaDto getCodesByCodesUri(
            @ApiParam(value = "Koodistoryhmän id") @PathVariable("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
            return conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("Error finding CodesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PutMapping
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Päivittää koodistoryhmää",
            notes = "",
            response = KoodistoRyhmaDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu päivitettyä")
    })
    public KoodistoRyhmaDto update(
            @ApiParam(value = "Koodistoryhmä") KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.UPDATE);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
            return conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid input for updating codesGroup. ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("Error while updating codesGroup. ", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping
    @JsonView({ JsonViews.Basic.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Luo uuden koodistoryhmän",
            notes = "",
            response = KoodistoRyhmaDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu lisättyä")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public KoodistoRyhmaDto insert(
            @ApiParam(value = "Koodistoryhmä") KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.INSERT);
            codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata((Collection) codesGroupDTO.getKoodistoRyhmaMetadatas()));
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. codesGroupDTO: " + codesGroupDTO);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("Error while inserting codesGroup.", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }

    @PostMapping("/delete/{id}")
    @JsonView({ JsonViews.Simple.class })
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    @ApiOperation(
            value = "Poistaa koodistoryhmän",
            notes = "",
            code= org.apache.http.HttpStatus.SC_ACCEPTED)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen."),
            @ApiResponse(code = 500, message = "Koodiryhmää ei saatu poistettua")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(
            @ApiParam(value = "Koodistoryhmän URI") @PathVariable("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            koodistoRyhmaBusinessService.delete(id);
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("Error while removing codesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
}
