package fi.vm.sade.koodisto.resource.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodiPageDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.views.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.resource.CodeElementResourceConverter;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoPageDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToKoodistoDtoConverter;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Hidden
@Validated
@RestController
@RequestMapping({"/internal/koodi"})
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InternalKoodiResource {

    private final KoodiBusinessService koodiBusinessService;
    private final KoodistoBusinessService koodistoBusinessService;
    private final CodeElementResourceConverter converter;
    private final KoodistoVersioToInternalKoodistoPageDtoConverter koodistoVersioToInternalKoodistoPageDtoConverter;
    private final KoodistoVersioToKoodistoDtoConverter koodistoVersioToKoodistoDtoConverter;
    private final KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter;
    private final KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;

    @GetMapping(path = "/{koodiUri}/{koodiVersio}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody
    ResponseEntity<InternalKoodiPageDto> getKoodi(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String koodiUri,
            @Parameter(description = "Koodin versio") @PathVariable @Min(1) final int koodiVersio
    ) {
        KoodiVersioWithKoodistoItem versio = koodiBusinessService.getKoodi(koodiUri, koodiVersio);
        ExtendedKoodiDto koodi = Optional.ofNullable(koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(versio)).orElseThrow(RuntimeException::new);
        KoodistoDto koodisto = koodistoVersioToKoodistoDtoConverter.convert(koodistoBusinessService.getKoodistoVersio(koodi.getKoodisto().getKoodistoUri(), Collections.max(koodi.getKoodisto().getKoodistoVersios())));
        koodi.setVersions(versio.getKoodiVersio().getKoodi().getKoodiVersios().size());
        return ResponseEntity.ok(new InternalKoodiPageDto(koodi, koodisto));
    }

    @GetMapping(path = "/{koodistoUri}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody
    ResponseEntity<List<KoodiDto>> getKoodiListForKoodisto(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri
    ) {
        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.getKoodisByKoodisto(koodistoUri, true);
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToKoodiDtoConverter.convertAll(result));
    }

    @PostMapping(path = "/{koodistoUri}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    @PreAuthorize("hasRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public ResponseEntity<InternalKoodistoPageDto> upsertKoodiByKoodisto(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @NotEmpty(message = "error.koodi.list.empty") @RequestBody List<@Valid KoodiDto> koodis
    ) {

        List<UpdateKoodiDataType> koodiList = koodis.stream()
                .map(koodi -> setKoodiUri(koodistoUri, koodi))
                .map(converter::convertFromDTOToUpdateKoodiDataType)
                .collect(Collectors.toList());
        KoodistoVersio koodisto = koodiBusinessService.massCreate(koodistoUri, koodiList);
        return ResponseEntity.ok(koodistoVersioToInternalKoodistoPageDtoConverter.convert(koodisto));

    }

    private KoodiDto setKoodiUri(String koodistoUri, KoodiDto koodi) {
        if (koodi.getKoodiUri() == null || koodi.getKoodiUri().isBlank()) {
            koodi.setKoodiUri(generateKoodiUri(koodistoUri, koodi));
        }
        return koodi;
    }

    private String generateKoodiUri(String koodistoUri, KoodiDto koodi) {
        return (koodistoUri + "_" + (koodi.getKoodiArvo().replaceAll("[^A-Za-z0-9]", "").toLowerCase()));
    }

}
