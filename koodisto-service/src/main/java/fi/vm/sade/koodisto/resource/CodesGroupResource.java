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
import fi.vm.sade.koodisto.validator.ValidationType;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/rest/codesgroup"})
public class CodesGroupResource {
    protected static final Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    private static final String GENERIC_ERROR_CODE = "error.codes.generic";

    private final KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    private final KoodistoConversionService conversionService;

    private final UriTransliterator uriTransliterator;

    private final CodesGroupValidator codesGroupValidator = new CodesGroupValidator();

    public CodesGroupResource(KoodistoRyhmaBusinessService koodistoRyhmaBusinessService, KoodistoConversionService conversionService, UriTransliterator uriTransliterator) {
        this.koodistoRyhmaBusinessService = koodistoRyhmaBusinessService;
        this.conversionService = conversionService;
        this.uriTransliterator = uriTransliterator;
    }

    @JsonView({ JsonViews.Basic.class })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Palauttaa koodistoryhmän")
    public ResponseEntity<Object> getCodesByCodesUri(
            @Parameter(description = "Koodistoryhman id") @PathVariable("id") Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
            return ResponseEntity.ok(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: {}", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error finding CodesGroup. id: {}", id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Basic.class })
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Päivittää koodistoryhmää")
    public ResponseEntity<Object> update(
            @Parameter(description = "Koodistoryhmä") @RequestBody KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.UPDATE);
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
            return ResponseEntity.status(201).body(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid input for updating codesGroup. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while updating codesGroup. ", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Basic.class })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Luo uuden koodistoryhmän")
    public ResponseEntity<Object> insert(
            @Parameter(description = "Koodistoryhmä") @RequestBody KoodistoRyhmaDto codesGroupDTO) {
        try {
            codesGroupValidator.validate(codesGroupDTO, ValidationType.INSERT);
            codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata(codesGroupDTO.getKoodistoRyhmaMetadatas()));
            KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
            return ResponseEntity.status(201).body(conversionService.convert(koodistoRyhma, KoodistoRyhmaDto.class));
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. codesGroupDTO: {}", codesGroupDTO);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while inserting codesGroup.", e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            return ResponseEntity.internalServerError().body(message);
        }
    }

    @JsonView({ JsonViews.Simple.class })
    @PostMapping(path = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Poistaa koodistoryhmän")
    public ResponseEntity<Object> delete(
            @Parameter(description = "Koodistoryhmän id") @PathVariable Long id) {
        try {
            String[] errors = { "id" };
            ValidatorUtil.validateArgs(errors, id);
            koodistoRyhmaBusinessService.delete(id);
            return ResponseEntity.accepted().build();
        } catch (KoodistoValidationException e) {
            logger.warn("Invalid parameter for rest call. id: {}", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.warn("Error while removing codesGroup. id: {}", id, e);
            String message = e instanceof SadeBusinessException ? e.getMessage() : GENERIC_ERROR_CODE;
            return ResponseEntity.internalServerError().body(message);
        }
    }
}
