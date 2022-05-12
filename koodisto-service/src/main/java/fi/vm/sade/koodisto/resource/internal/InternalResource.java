package fi.vm.sade.koodisto.resource.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.resource.CodeElementResourceConverter;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
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
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Hidden
@Validated
@RestController
@RequestMapping({"/internal"})
@RequiredArgsConstructor
public class InternalResource {


    private final KoodiBusinessService koodiBusinessService;
    private final KoodistoConversionService conversionService;
    private final CodeElementResourceConverter converter;

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @GetMapping(path = "/koodi/{koodistoUri}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Extended.class})
    public @ResponseBody ResponseEntity<List<KoodiDto>> getKoodiBatch(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri
    ) {
        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.getKoodisByKoodisto(koodistoUri, true);
        return ResponseEntity.ok(conversionService.convertAll(result, KoodiDto.class));
    }

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/koodi/{koodistoUri}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Extended.class})
    public ResponseEntity<Object> createKoodiBatch(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri, @NotEmpty(message = "error.koodi.list.empty") @RequestBody List<@Valid KoodiDto> koodis
    ) {

        List<UpdateKoodiDataType> koodiList = koodis.stream()
                .map(koodi -> validateAndSet(koodistoUri, koodi))
                .map(converter::convertFromDTOToUpdateKoodiDataType)
                .collect(Collectors.toList());
        KoodistoVersio koodisto = koodiBusinessService.massCreate(koodistoUri, koodiList);
        return ResponseEntity.ok(conversionService.convert(koodisto, KoodistoDto.class));

    }

    private KoodiDto validateAndSet(String koodistoUri, KoodiDto koodi) {
        if (koodi.getKoodiUri() == null || koodi.getKoodiUri().isBlank()) {
            koodi.setKoodiUri(getKoodiUri(koodistoUri, koodi));
        }
        koodi.setVersion((long) koodi.getVersio());
        return koodi;
    }

    private String getKoodiUri(String koodistoUri, KoodiDto koodi) {
        return (koodistoUri + "_" + (koodi.getKoodiArvo().replaceAll("[^A-Za-z0-9]", "").toLowerCase()));
    }

}
