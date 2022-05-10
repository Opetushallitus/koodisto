package fi.vm.sade.koodisto.resource.internal;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.resource.CodeElementResourceConverter;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiArvoEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoImportException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Hidden
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
        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.getKoodisByKoodisto(koodistoUri,true);
        return ResponseEntity.ok(conversionService.convertAll(result, KoodiDto.class));
    }

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/koodi/{koodistoUri}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Extended.class})
    public ResponseEntity<Object> createKoodiBatch(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri, @RequestBody List<KoodiDto> koodis
    ) {
        if (koodis == null || koodis.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<UpdateKoodiDataType> koodiList = koodis.stream()
                    .map(koodi -> validateAndSet(koodistoUri, koodi))
                    .map(converter::convertFromDTOToUpdateKoodiDataType)
                    .collect(Collectors.toList());
            KoodistoVersio koodisto = koodiBusinessService.massCreate(koodistoUri, koodiList);
            return ResponseEntity.ok(conversionService.convert(koodisto, KoodistoDto.class));
        } catch (KoodistoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private KoodiDto validateAndSet(String koodistoUri, KoodiDto koodi) {
        ValidatorUtil.checkForBlank(koodi.getKoodiArvo(), new KoodistoImportException("error.koodiarvo.empty"));
        ValidatorUtil.checkCollectionIsNotNullOrEmpty(koodi.getMetadata(), new KoodistoImportException("error.metadata.empty"));
        ValidatorUtil.checkForBlank(koodi.getMetadata().get(0).getNimi(), new KoodistoImportException("error.nimi.empty"));
        if (koodi.getVoimassaAlkuPvm() == null) {
            koodi.setVoimassaAlkuPvm(new Date());
        }
        if (koodi.getKoodiUri() == null || koodi.getKoodiUri().isBlank()) {
            ValidatorUtil.checkForBlank(koodi.getKoodiArvo(), new KoodiArvoEmptyException());
            koodi.setKoodiUri(getKoodiUri(koodistoUri, koodi));
        }
        if (koodi.getVersio() == 0) {
            koodi.setVersio(1);
        }
        if (koodi.getTila() == null) {
            koodi.setTila(Tila.LUONNOS);
        }
        koodi.setVersion((long) koodi.getVersio());
        return koodi;
    }

    private String getKoodiUri(String koodistoUri, KoodiDto koodi) {
        return (koodistoUri + "_" + (koodi.getKoodiArvo().replaceAll("[^A-Za-z0-9]", "").toLowerCase()));
    }

}
