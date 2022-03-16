package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.javautils.opintopolku_spring_security.SadeBusinessException;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.validator.CodesGroupValidator;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.validator.Validatable.ValidationType;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping({"/codesgroup"})
//@Api(value = "/rest/codesgroup", description = "Koodistoryhmät")
public class CodesGroupResource {
    protected final static Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    @Autowired
    private KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    @Autowired
    private KoodistoConversionService conversionService;

    @Autowired
    private UriTransliterator uriTransliterator;

    private CodesGroupValidator codesGroupValidator = new CodesGroupValidator();

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    /*@ApiOperation(
            value = "Palauttaa koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei löydy kyseisellä id:llä")
    })*/
    public ResponseEntity getCodesByCodesUri(
            @PathVariable("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
            return ResponseEntity.ok(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error finding CodesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Päivittää koodistoryhmää",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu päivitettyä")
    })*/
    public ResponseEntity update(
            @RequestBody KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.UPDATE);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
            return ResponseEntity.status(201).body(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid input for updating codesGroup. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while updating codesGroup. ", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Luo uuden koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK"),
            @ApiResponse(code = 400, message = "Parametri on tyhjä"),
            @ApiResponse(code = 500, message = "Koodistoryhmää ei saatu lisättyä")
    })*/
    public ResponseEntity insert(
            @RequestBody KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.INSERT);
            codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata((Collection) codesGroupDTO.getKoodistoRyhmaMetadatas()));
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return ResponseEntity.status(201).body(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. codesGroupDTO: " + codesGroupDTO);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while inserting codesGroup.", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @PostMapping(path = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Poistaa koodistoryhmän",
            notes = "",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "OK"),
            @ApiResponse(code = 400, message = "Id on virheellinen."),
            @ApiResponse(code = 500, message = "Koodiryhmää ei saatu poistettua")
    })*/
    public ResponseEntity delete(
            @PathVariable Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            koodistoRyhmaBusinessService.delete(id);
            return ResponseEntity.accepted().build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: " + id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while removing codesGroup. id: " + id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : "error.codes.generic";
            return ResponseEntity.internalServerError().body(message);
        }
    }
}
