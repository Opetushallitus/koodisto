package fi.vm.sade.koodisto.resource.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoListDto;
import fi.vm.sade.koodisto.dto.internal.InternalKoodistoPageDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.InternalKoodistoPageDtoToCreateKoodistoDataTypeConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoListDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToInternalKoodistoPageDtoConverter;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Hidden
@Validated
@RestController
@RequestMapping({"/internal/koodisto"})
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InternalKoodistoResource {


    private final KoodistoBusinessService koodistoBusinessService;
    private final KoodistoVersioToInternalKoodistoListDtoConverter koodistoVersioToInternalKoodistoListDtoConverter;
    private final KoodistoVersioToInternalKoodistoPageDtoConverter koodistoVersioToInternalKoodistoPageDtoConverter;
    private final InternalKoodistoPageDtoToCreateKoodistoDataTypeConverter internalKoodistoPageDtoToCreateKoodistoDataTypeConverter;

    @GetMapping(path = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody ResponseEntity<List<InternalKoodistoListDto>> getKoodistoList() {
        SearchKoodistosCriteriaType criteria = KoodistoServiceSearchCriteriaBuilder.latestCodes();
        List<KoodistoVersio> result = koodistoBusinessService.searchKoodistos(criteria);
        return ResponseEntity.ok(koodistoVersioToInternalKoodistoListDtoConverter.convertAll(result));
    }

    @GetMapping(path = "/{koodistoUri}/{koodistoVersio}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    public @ResponseBody ResponseEntity<InternalKoodistoPageDto> getKoodisto(
            @PathVariable @NotBlank String koodistoUri,
            @PathVariable @Min(1) Integer koodistoVersio) {
        KoodistoVersio result = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        return ResponseEntity.ok(koodistoVersioToInternalKoodistoPageDtoConverter.convert(result));
    }

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Internal.class})
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public @ResponseBody ResponseEntity<InternalKoodistoPageDto> updateKoodisto(
            @RequestBody @Valid UpdateKoodistoDataType koodisto) {
        KoodistoVersio result = koodistoBusinessService.updateKoodisto(koodisto);
        return ResponseEntity.ok(koodistoVersioToInternalKoodistoPageDtoConverter.convert(result));
    }

    @PostMapping(path = "/{koodistoRyhmaUri}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public @ResponseBody ResponseEntity<InternalKoodistoPageDto> createKoodisto(
            @PathVariable @NotBlank String koodistoRyhmaUri,
            @RequestBody @Valid CreateKoodistoDataType koodisto) {
        KoodistoVersio result = koodistoBusinessService.createKoodisto(List.of(koodistoRyhmaUri), koodisto);
        return ResponseEntity.ok(koodistoVersioToInternalKoodistoPageDtoConverter.convert(result));
    }
}
