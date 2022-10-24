package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToKoodiDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.model.JsonViews;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping({"/rest/codeelement"})
@RequiredArgsConstructor
public class CodeElementResource {

    final KoodiChangesService changesService;
    private final KoodiBusinessService koodiBusinessService;
    private final CodeElementResourceConverter codeElementResourceConverter;
    private final KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter koodiVersioWithKoodistoItemToSimpleKoodiDtoConverter;
    private final KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;
    private final KoodiVersioWithKoodistoItemToKoodiDtoConverter koodiVersioWithKoodistoItemToKoodiDtoConverter;

    @JsonView({JsonViews.Simple.class}) // tarvitaanko?
    @Operation(description = "Palauttaa koodiversiot tietystä koodista")
    @GetMapping(path = "/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllCodeElementVersionsByCodeElementUri(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty String codeElementUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(codeElementUri);
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToSimpleKoodiDtoConverter.convertAll(codeElements));
    }

    @GetMapping(path = "/{koodiUri}/{koodiVersio}", produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView({JsonViews.Extended.class})
    @Transactional(readOnly = true)
    @Operation(
            description = "Palauttaa tietyn koodiversion",
            summary = "sisältää koodiversion koodinsuhteet"
    )
    public ResponseEntity<Object> getCodeElementByUriAndVersion(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String koodiUri,
            @Parameter(description = "Koodin versio") @PathVariable @Min(1) final int koodiVersio) {
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(koodiBusinessService.getKoodi(koodiUri, koodiVersio)));
    }

    @GetMapping(path = "/{codesUri}/{codesVersion}/{codeElementUri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @JsonView({JsonViews.Basic.class})
    @Operation(description = "Palauttaa koodin tietystä koodistoversiosta")
    public ResponseEntity<Object> getCodeElementByCodeElementUri(
            @Parameter(description = "Koodiston URI") @PathVariable @NotEmpty final String codesUri,
            @Parameter(description = "Koodiston versio") @PathVariable @Min(1) final int codesVersion,
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String codeElementUri) {
        KoodiVersioWithKoodistoItem codeElement = koodiBusinessService.getKoodiByKoodistoVersio(codesUri, codesVersion, codeElementUri);
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(codeElement));
    }

    @JsonView({JsonViews.Simple.class})
    @Operation(description = "Palauttaa koodit tietystä koodistoversiosta")
    @GetMapping(path = "/codes/{codesUri}/{codesVersion}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Object> getAllCodeElementsByCodesUriAndVersion(
            @Parameter(description = "Koodisto URI") @PathVariable @NotEmpty final String codesUri,
            @Parameter(description = "Koodiston versio") @PathVariable @Min(1) final int codesVersion) {
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.getKoodisByKoodistoVersio(codesUri, codesVersion, false);
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToSimpleKoodiDtoConverter.convertAll(codeElements));
    }

    @JsonView({JsonViews.Simple.class})
    @Operation(description = "Palauttaa koodit viimeisestä koodistoversiosta")
    @GetMapping(path = "/codes/{codesUri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Object> getAllCodeElementsByCodesUri(
            @Parameter(description = "Koodisto URI") @PathVariable @NotEmpty final String codesUri) {
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.getKoodisByKoodisto(codesUri, false);
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToSimpleKoodiDtoConverter.convertAll(codeElements));
    }

    @Operation(description = "Palauttaa uusimman koodiversion")
    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/latest/{codeElementUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLatestCodeElementVersionsByCodeElementUri(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String codeElementUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(codeElementUri);
        List<KoodiVersioWithKoodistoItem> codeElements = koodiBusinessService.searchKoodis(searchType);
        if (codeElements.isEmpty()) {
            throw new KoodiNotFoundException();
        }
        return ResponseEntity.ok(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(codeElements.get(0)));
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(
            summary = "Palauttaa muutokset uusimpaan koodiversioon",
            description = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.")
    @GetMapping(path = "/changes/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodeElement(@Parameter(description = "Koodin URI") @PathVariable final String codeElementUri,
                                                          @Parameter(description = "Koodin versio") @PathVariable @Min(1) final Integer codeElementVersion,
                                                          @Parameter(description = "Verrataanko viimeiseen hyväksyttyyn versioon") @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) {
        return ResponseEntity.ok(changesService.getChangesDto(codeElementUri, codeElementVersion, compareToLatestAccepted));
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(description = "Palauttaa tehdyt muutokset uusimpaan koodiversioon käyttäen lähintä päivämäärään osuvaa koodiversiota vertailussa",
            summary = "Toimii vain, jos koodi on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.")
    @GetMapping(path = "/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodeElementWithDate(@PathVariable String codeElementUri,
                                                                  @Parameter(description = "Kuukauden päivä") @PathVariable @Min(1) @Max(31) final int dayofmonth,
                                                                  @Parameter(description = "Kuukausi") @PathVariable @Min(1) @Max(12) final int month,
                                                                  @Parameter(description = "Vuosi") @PathVariable @Min(1) final int year,
                                                                  @Parameter(description = "Tunti") @PathVariable @Min(0) @Max(23) final int hour,
                                                                  @Parameter(description = "Minuutti") @PathVariable @Min(0) @Max(59) final int minute,
                                                                  @Parameter(description = "Sekunti") @PathVariable @Min(0) @Max(59) final int second,
                                                                  @Parameter(description = "Verrataanko viimeiseen hyväksyttyyn versioon") @RequestParam(defaultValue = "false") Boolean compareToLatestAccepted) {
        DateTime dateTime = new DateTime(year, month, dayofmonth, hour, minute, second);
        return ResponseEntity.ok(changesService.getChangesDto(codeElementUri, dateTime, compareToLatestAccepted));
    }

    @JsonView({JsonViews.Basic.class})
    @Operation(description = "Lisää uuden koodin")
    @PostMapping(path = "/{codesUri}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    public ResponseEntity<Object> insert(
            @Parameter(description = "Koodiston URI") @PathVariable @NotEmpty final String codesUri,
            @Parameter(description = "Koodi") @RequestBody @Valid final KoodiDto codeelementDTO) {
        // aika huono toteutus
        KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem = koodiBusinessService.createKoodi(codesUri,
                codeElementResourceConverter.convertFromDTOToCreateKoodiDataType(codeelementDTO));
        return ResponseEntity.status(201).body(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersioWithKoodistoItem));
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(description = "Lisää relaation koodien välille")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addRelation(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String codeElementUri,
            @Parameter(description = "Linkitettävän koodin URI") @PathVariable @NotEmpty final String codeElementUriToAdd,
            @Parameter(description = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable final SuhteenTyyppi relationType) {
        koodiBusinessService.addRelation(codeElementUri, List.of(codeElementUriToAdd), relationType, false);
        return ResponseEntity.ok(null);
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(description = "Lisää koodien välisiä relaatioita, massatoiminto")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/addrelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addRelations(
            @Parameter(description = "Relaation tiedot JSON muodossa") @RequestBody @Valid final KoodiRelaatioListaDto koodiRelaatioDto
    ) {
        koodiBusinessService.addRelation(koodiRelaatioDto);
        return ResponseEntity.ok(null);
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(
            description = "Poistaa koodien välisen relaation")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeRelation(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String codeElementUri,
            @Parameter(description = "Irroitettavan koodin URI") @PathVariable @NotEmpty final String codeElementUriToRemove,
            @Parameter(description = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable final SuhteenTyyppi relationType) {
        koodiBusinessService.removeRelation(codeElementUri, List.of(codeElementUriToRemove),
                relationType, false);
        return ResponseEntity.ok(null);
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(description = "Poistaa koodien välisiä relaatioita, massatoiminto")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PostMapping(path = "/removerelations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeRelations(
            @Parameter(description = "Relaation tiedot JSON muodossa") @RequestBody @Valid final KoodiRelaatioListaDto koodiRelaatioDto
    ) {
        koodiBusinessService.removeRelation(koodiRelaatioDto);
        return ResponseEntity.ok(null);
    }

    // pitääis olla delete method
    @JsonView({JsonViews.Simple.class})
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Poistaa koodin")
    @PostMapping(path = "/delete/{codeElementUri}/{codeElementVersion}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(
            @Parameter(description = "Koodin URI") @PathVariable @NotEmpty final String codeElementUri,
            @Parameter(description = "Koodin versio") @PathVariable @Min(1) final int codeElementVersion) {
        koodiBusinessService.delete(codeElementUri, codeElementVersion);
        return ResponseEntity.status(202).body(null);
    }

    @JsonView({JsonViews.Extended.class})
    @Operation(description = "Päivittää koodin")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PutMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @Parameter(description = "Koodi") @RequestBody @Valid final KoodiDto codeElementDTO) {
        if (Optional.ofNullable(codeElementDTO.getKoodiUri()).map(String::isBlank).orElse(true)) {
            throw new KoodistoValidationException("error.validation.codeelementuri");
        }
        KoodiVersioWithKoodistoItem koodiVersio =
                koodiBusinessService.updateKoodi(codeElementResourceConverter.convertFromDTOToUpdateKoodiDataType(codeElementDTO));
        return ResponseEntity.status(201).body(koodiVersioWithKoodistoItemToKoodiDtoConverter.convert(koodiVersio));
    }

    @JsonView({JsonViews.Basic.class})
    @Operation(description = "Päivittää koodin kokonaisuutena", summary = "Lisää ja poistaa koodinsuhteita vastaamaan annettua koodia.")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @PutMapping(path = "/save", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(
            @Parameter(description = "Koodi") @RequestBody @Valid final ExtendedKoodiDto koodiDTO) {
        KoodiVersio koodiVersio = koodiBusinessService.saveKoodi(koodiDTO);
        return ResponseEntity.ok(koodiVersio.getVersio().toString());
    }
}
