package fi.vm.sade.koodisto.resource.internal;

import fi.vm.sade.koodisto.dto.internal.InternalInsertKoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Hidden
@Validated
@RestController
@RequestMapping({"/internal/koodistoryhma"})
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InternalKoodistoRyhmaResource {

    private final KoodistoRyhmaBusinessService koodistoRyhmaBusinessService;
    private final KoodistoConversionService conversionService;

    @PostMapping(path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public ResponseEntity<InternalKoodistoRyhmaDto> insertKoodistoRyhma(@RequestBody @Valid InternalInsertKoodistoRyhmaDto insertKoodistoRyhma) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.createKoodistoRyhma(insertKoodistoRyhma);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversionService.convert(koodistoRyhma, InternalKoodistoRyhmaDto.class));
    }
    @GetMapping(path = "/{koodistoRyhmaUri}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InternalKoodistoRyhmaDto> getKoodistoRyhma(@PathVariable String koodistoRyhmaUri) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.getKoodistoRyhmaByUri(koodistoRyhmaUri);
        return ResponseEntity.ok(conversionService.convert(koodistoRyhma, InternalKoodistoRyhmaDto.class));
    }
    @PutMapping(path = "/{koodistoRyhmaUri}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public ResponseEntity<InternalKoodistoRyhmaDto> updateKoodistoRyhma(
            @PathVariable String koodistoRyhmaUri, @RequestBody @Valid InternalInsertKoodistoRyhmaDto insertKoodistoRyhma) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaBusinessService.updateKoodistoRyhma(koodistoRyhmaUri, insertKoodistoRyhma);
        return ResponseEntity.ok(conversionService.convert(koodistoRyhma, InternalKoodistoRyhmaDto.class));
    }

    @DeleteMapping(path = "/{koodistoRyhmaUri}")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public ResponseEntity<Void> deleteKoodistoRyhma(@PathVariable String koodistoRyhmaUri) {
        koodistoRyhmaBusinessService.delete(koodistoRyhmaUri);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empty")
    public ResponseEntity<List<InternalKoodistoRyhmaDto>> getEmptyKoodistoRyhma() {
        return ResponseEntity.ok(conversionService.convertAll(koodistoRyhmaBusinessService.getEmptyKoodistoRyhma(), InternalKoodistoRyhmaDto.class));

    }
}
