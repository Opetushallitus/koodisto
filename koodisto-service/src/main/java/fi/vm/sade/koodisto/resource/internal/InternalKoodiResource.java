package fi.vm.sade.koodisto.resource.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodiVersioListDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.resource.CodeElementResourceConverter;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoValidationException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.*;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoPageDtoConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Hidden
@Validated
@RestController
@RequestMapping({"/internal/koodi"})
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InternalKoodiResource {

    private final KoodistoBusinessService koodistoBusinessService;
    private final KoodiBusinessService koodiBusinessService;
    private final CodeElementResourceConverter converter;
    private final KoodistoVersioToInternalKoodistoPageDtoConverter koodistoVersioToInternalKoodistoPageDtoConverter;
    private final KoodiMetadataToKoodiMetadataDtoConverter koodiMetadataToKoodiMetadataDtoConverter;
    private final KoodiVersioToInternalKoodiVersioDtoConverter koodiVersioToInternalKoodiVersioDtoConverter;
    private final KoodiVersioToInternalKoodiVersioListDtoConverter koodiVersioToInternalKoodiVersioListDtoConverter;

    @GetMapping(path = "/{koodiUri}/{koodiVersio}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody
    ResponseEntity<InternalKoodiVersioDto> getKoodi(
            @Parameter(description = "Koodin URI") @PathVariable final String koodiUri,
            @Parameter(description = "Koodin versio") @PathVariable @Min(1) final int koodiVersio
    ) {
        KoodiVersioWithKoodistoItem versio = koodiBusinessService.getKoodi(koodiUri, koodiVersio);
        return ResponseEntity.ok(koodiVersioToInternalKoodiVersioDtoConverter.convert(versio.getKoodiVersio()));
    }

    @DeleteMapping(path = "/{koodiUri}/{koodiVersio}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public @ResponseBody
    ResponseEntity<Void> deleteKoodi(
            @Parameter(description = "Koodin URI") @PathVariable final String koodiUri,
            @Parameter(description = "Koodin versio") @PathVariable @Min(1) final int koodiVersio
    ) {
        KoodiVersio koodi = koodiBusinessService.getKoodi(koodiUri, koodiVersio).getKoodiVersio();
        if (koodi.isLocked()) {
            throw new KoodistoValidationException("error.codes.version.locked");
        }

        koodiBusinessService.forceDelete(koodiUri, koodiVersio);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/koodisto/{koodistoUri}/{koodistoVersio}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody
    ResponseEntity<List<InternalKoodiVersioListDto>> getKoodistoKoodis(
            @PathVariable String koodistoUri,
            @PathVariable @Min(1) Integer koodistoVersio) {
        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.getKoodisByKoodistoVersio(koodistoUri, koodistoVersio, false);
        return ResponseEntity.ok(koodiVersioToInternalKoodiVersioListDtoConverter.convertAll(
                result.stream().map(KoodiVersioWithKoodistoItem::getKoodiVersio).collect(Collectors.toList())));
    }

    @PostMapping(path = "/upsert/{koodistoUri}",
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

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    @PreAuthorize("hasRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public @ResponseBody
    ResponseEntity<InternalKoodiVersioDto> updateKoodi(
            @RequestBody @Valid InternalKoodiVersioDto koodi) {
        return ResponseEntity.ok(koodiBusinessService.updateKoodi(koodi));
    }

    @PostMapping(path = "/{koodistoUri}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    @PreAuthorize("hasRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public @ResponseBody
    ResponseEntity<InternalKoodiVersioDto> createKoodi(
            @PathVariable String koodistoUri,
            @RequestBody @Valid CreateKoodiDataType koodi) {
        KoodiVersioWithKoodistoItem result = koodiBusinessService.createKoodi((koodistoUri), koodi);
        InternalKoodiVersioDto converted = convertKoodiVersioWithKoodistoItemToInternalKoodiVersioDto(result);
        converted.setKoodisto(koodistoVersioToInternalKoodistoPageDtoConverter.convert(koodistoBusinessService.getLatestKoodistoVersio(koodistoUri)));
        return ResponseEntity.ok(converted);
    }

    private InternalKoodiVersioDto convertKoodiVersioWithKoodistoItemToInternalKoodiVersioDto(KoodiVersioWithKoodistoItem source) {
        return InternalKoodiVersioDto.internalKoodiVersioDtoBuilder()
                        .koodiArvo(source.getKoodiVersio().getKoodiarvo())
                        .versio(source.getKoodiVersio().getVersio())
                        .lockingVersion(source.getKoodiVersio().getVersion())
                        .tila(source.getKoodiVersio().getTila())
                        .koodiUri(source.getKoodiVersio().getKoodi().getKoodiUri())
                        .paivitysPvm(source.getKoodiVersio().getPaivitysPvm())
                        .paivittajaOid(source.getKoodiVersio().getPaivittajaOid())
                        .voimassaAlkuPvm(source.getKoodiVersio().getVoimassaAlkuPvm())
                        .voimassaLoppuPvm(source.getKoodiVersio().getVoimassaLoppuPvm())
                        .metadata(source.getKoodiVersio().getMetadatas().stream()
                                .map(koodiMetadataToKoodiMetadataDtoConverter::convert)
                                .collect(Collectors.toList()))
                        .rinnastuuKoodeihin(KoodiConverterUtil.getLevelsWithCodes(source.getKoodiVersio()))
                        .sisaltaaKoodit(KoodiConverterUtil.extractBySuhde(source.getKoodiVersio().getAlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getAlakoodiVersio))
                        .sisaltyyKoodeihin(KoodiConverterUtil.extractBySuhde(source.getKoodiVersio().getYlakoodis(), SuhteenTyyppi.SISALTYY, KoodinSuhde::getYlakoodiVersio))
                        .build();
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
