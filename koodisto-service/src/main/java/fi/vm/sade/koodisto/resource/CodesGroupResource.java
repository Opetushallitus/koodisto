package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoRyhmaToKoodistoRyhmaDtoConverter;
import fi.vm.sade.koodisto.validator.CodesGroupValidator;
import fi.vm.sade.koodisto.validator.ValidationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping({"/rest/codesgroup"})
@RequiredArgsConstructor
public class CodesGroupResource {
    protected static final Logger logger = LoggerFactory.getLogger(CodesGroupResource.class);

    private final KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;

    private final KoodistoRyhmaToKoodistoRyhmaDtoConverter koodistoRyhmaToKoodistoRyhmaDtoConverter;

    private final UriTransliterator uriTransliterator;

    private final CodesGroupValidator codesGroupValidator = new CodesGroupValidator();

    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Palauttaa koodistoryhmän")
    public ResponseEntity<Object> getCodesByCodesUri(
            @Parameter(description = "Koodistoryhman id") @PathVariable("id") final long id) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaById(id);
        return ResponseEntity.ok(koodistoRyhmaToKoodistoRyhmaDtoConverter.convert(koodistoRyhma));
    }

    @JsonView({JsonViews.Basic.class})
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Päivittää koodistoryhmää")
    public ResponseEntity<Object> update(
            @Parameter(description = "Koodistoryhmä") @RequestBody @Valid KoodistoRyhmaDto codesGroupDTO) {
        codesGroupValidator.validate(codesGroupDTO, ValidationType.UPDATE);
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(codesGroupDTO);
        return ResponseEntity.status(201).body(koodistoRyhmaToKoodistoRyhmaDtoConverter.convert(koodistoRyhma));
    }

    @JsonView({JsonViews.Basic.class})
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Luo uuden koodistoryhmän")
    public ResponseEntity<Object> insert(
            @Parameter(description = "Koodistoryhmä") @RequestBody @Valid KoodistoRyhmaDto codesGroupDTO) {
        codesGroupValidator.validate(codesGroupDTO, ValidationType.INSERT);
        codesGroupDTO.setKoodistoRyhmaUri(uriTransliterator.generateKoodistoGroupUriByMetadata(codesGroupDTO.getKoodistoRyhmaMetadatas()));
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(codesGroupDTO);
        return ResponseEntity.status(201).body(koodistoRyhmaToKoodistoRyhmaDtoConverter.convert(koodistoRyhma));
    }

    @JsonView({JsonViews.Simple.class})
    @PostMapping(path = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Poistaa koodistoryhmän")
    public ResponseEntity<Object> delete(
            @Parameter(description = "Koodistoryhmän id") @PathVariable final long id) {
        koodistoRyhmaBusinessService.delete(id);
        return ResponseEntity.accepted().build();
    }
}
