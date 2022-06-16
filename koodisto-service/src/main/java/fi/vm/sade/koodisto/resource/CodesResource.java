package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoToKoodistoListDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToKoodistoDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.KoodistoVersioToKoodistoVersioListDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaToKoodistoRyhmaListDtoConverter;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.validator.CodesValidator;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.validator.ValidationType;
import fi.vm.sade.koodisto.validator.ValidatorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/rest/codes"})
@RequiredArgsConstructor
public class CodesResource {

    private static final Logger logger = LoggerFactory.getLogger(CodesResource.class);
    private static final String KOODISTOURI = "codesuri";
    private static final String KOODISTOVERSIO = "codesversion";
    private static final String RELATIONTYPE = "relationtype";
    private static final String GENERIC_ERROR_CODE = "error.codes.generic";
    private final KoodistoBusinessService koodistoBusinessService;
    
    private final CodesResourceConverter converter;
    private final KoodistoChangesService changesService;
    private final CodesValidator codesValidator = new CodesValidator();

    private final KoodistoVersioToKoodistoDtoConverter koodistoVersioToKoodistoDtoConverter;
    private final KoodistoVersioToKoodistoVersioListDtoConverter koodistoVersioToKoodistoVersioListDtoConverter;
    private final KoodistoRyhmaToKoodistoRyhmaListDtoConverter koodistoRyhmaToKoodistoRyhmaListDtoConverter;
    private final KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter;

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @JsonView({JsonViews.Extended.class})
    @PostMapping(path = "/addrelation/{codesUri}/{codesUriToAdd}/{relationType}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Lisää relaatio koodistojen välille")
    public ResponseEntity<Object> addRelation(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Linkitettävän koodiston URI") @PathVariable String codesUriToAdd,
            @Parameter(description = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable String relationType) {

            String[] errors = {KOODISTOURI, "codesuritoadd", RELATIONTYPE};
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToAdd, relationType);

            koodistoBusinessService.addRelation(codesUri, codesUriToAdd, SuhteenTyyppi.valueOf(relationType));
            return ResponseEntity.ok(null);

    }

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @JsonView({JsonViews.Extended.class})
    @PostMapping(path = "/removerelation/{codesUri}/{codesUriToRemove}/{relationType}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Poistaa relaatio koodistojen väliltä")
    public ResponseEntity<Object> removeRelation(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Irrotettavan koodiston URI") @PathVariable String codesUriToRemove,
            @Parameter(description = "Relaation tyyppi (SISALTYY, RINNASTEINEN)") @PathVariable String relationType) {

            String[] errors = {KOODISTOURI, "codesuritoremove", RELATIONTYPE};
            ValidatorUtil.validateArgs(errors, codesUri, codesUriToRemove, relationType);
            koodistoBusinessService.removeRelation(codesUri, List.of(codesUriToRemove), SuhteenTyyppi.valueOf(relationType));
            return ResponseEntity.ok(null);

    }


    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @JsonView({JsonViews.Basic.class})
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Päivittää koodistoa")
    public ResponseEntity<Object> update(
            @Parameter(description = "Koodisto") @RequestBody KoodistoDto codesDTO) {
            codesValidator.validate(codesDTO, ValidationType.UPDATE);
            KoodistoVersio koodistoVersio = koodistoBusinessService.updateKoodisto(converter.convertFromDTOToUpdateKoodistoDataType(codesDTO));
            return ResponseEntity.status(201).body(koodistoVersio.getVersio());
  }

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Päivittää koodiston kokonaisuutena",
            summary = "Lisää ja poistaa koodistonsuhteita")
    @JsonView({JsonViews.Basic.class})
    @PutMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(
            @Parameter(description = "Koodisto") @RequestBody KoodistoDto codesDTO) {
             codesValidator.validate(codesDTO, ValidationType.UPDATE);
            KoodistoVersio koodistoVersio = koodistoBusinessService.saveKoodisto(codesDTO);
            return ResponseEntity.ok(koodistoVersio.getVersio().toString());

      }

    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @JsonView({JsonViews.Basic.class})
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Lisää koodiston")
    public ResponseEntity<Object> insert(
            @Parameter(description = "Koodisto") @RequestBody KoodistoDto codesDTO) {
            codesValidator.validate(codesDTO, ValidationType.INSERT);
            List<String> codesGroupUris = new ArrayList<>();
            codesGroupUris.add(codesDTO.getCodesGroupUri());
            KoodistoVersio koodistoVersio = koodistoBusinessService.createKoodisto(codesGroupUris, converter.convertFromDTOToCreateKoodistoDataType(codesDTO));
            return ResponseEntity.status(201).body(koodistoVersioToKoodistoDtoConverter.convert(koodistoVersio));
      }


    @Operation(description = "Palauttaa kaikki koodistoryhmät")
    @JsonView(JsonViews.Simple.class)
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Object> listAllCodesGroups() {
            return ResponseEntity.ok(koodistoRyhmaToKoodistoRyhmaListDtoConverter.convertAll(koodistoBusinessService.listAllKoodistoRyhmas()));
      }

    @Operation(description = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot")
    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listAllCodesInAllCodeGroups() {
             SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestCodes();
            return ResponseEntity.ok(koodistoVersioToKoodistoVersioListDtoConverter.convertAll(koodistoBusinessService.searchKoodistos(searchType)));
      }

    @Operation(description = "Palauttaa koodiston")
    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/{codesUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCodesByCodesUri(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri) {
            String[] errors = {KOODISTOURI};
            ValidatorUtil.validateArgs(errors, codesUri);
            Koodisto koodisto = koodistoBusinessService.getKoodistoByKoodistoUri(codesUri);
            return ResponseEntity.ok(koodistoToKoodistoListDtoConverter.convert(koodisto));
     }

    @Operation(description = "Palauttaa tietyn koodistoversion")
    @JsonView({JsonViews.Extended.class})
    @GetMapping(path = "/{codesUri}/{codesVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCodesByCodesUriAndVersion(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Koodiston Versio") @PathVariable int codesVersion) {
            String[] errors = {KOODISTOURI, KOODISTOVERSIO};
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            KoodistoVersio koodistoVersio;
            if (codesVersion == 0) {
                koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(codesUri);
            } else {
                koodistoVersio = koodistoBusinessService.getKoodistoVersio(codesUri, codesVersion);
            }
            return ResponseEntity.ok(koodistoVersioToKoodistoDtoConverter.convert(koodistoVersio));

     }

    @Operation(description = "Palauttaa muutokset uusimpaan koodistoversioon verrattaessa",
            summary = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.")
    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/changes/{codesUri}/{codesVersion}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodes(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Koodiston Versio") @PathVariable Integer codesVersion,
            @Parameter(description = "Verrataanko viimeiseen hyväksyttyyn versioon") @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) {
            ValidatorUtil.checkForGreaterThan(codesVersion, 0, new KoodistoValidationException("error.validation.codeelementversion"));
            return ResponseEntity.ok(changesService.getChangesDto(codesUri, codesVersion, compareToLatestAccepted));
      }


    @Operation(description = "Palauttaa tehdyt muutokset uusimpaan koodistoversioon käyttäen lähintä päivämäärään osuvaa koodistoversiota vertailussa",
            summary = "Toimii vain, jos koodisto on versioitunut muutoksista, eli sitä ei ole jätetty luonnostilaan.")
    @JsonView({JsonViews.Basic.class})
    @GetMapping(path = "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getChangesToCodesWithDate(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Kuukauden päivä") @PathVariable Integer dayofmonth,
            @Parameter(description = "Kuukausi") @PathVariable Integer month,
            @Parameter(description = "Vuosi") @PathVariable Integer year,
            @Parameter(description = "Tunti") @PathVariable Integer hour,
            @Parameter(description = "Minuutti") @PathVariable Integer minute,
            @Parameter(description = "Sekunti") @PathVariable Integer second,
            @Parameter(description = "Verrataanko viimeiseen hyväksyttyyn versioon") @RequestParam(defaultValue = "false") boolean compareToLatestAccepted) {
            ValidatorUtil.validateDateParameters(dayofmonth, month, year, hour, minute, second);
            DateTime date = new DateTime(year, month, dayofmonth, hour, minute, second);
            return ResponseEntity.ok(changesService.getChangesDto(codesUri, date, compareToLatestAccepted));
      }

    // pitäs olla delete
    @Operation(description = "Poistaa koodiston")
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @JsonView({JsonViews.Simple.class})
    @PostMapping(path = "/delete/{codesUri}/{codesVersion}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(
            @Parameter(description = "Koodiston URI") @PathVariable String codesUri,
            @Parameter(description = "Koodiston Versio") @PathVariable int codesVersion) {
            String[] errors = {KOODISTOURI, KOODISTOVERSIO};
            ValidatorUtil.validateArgs(errors, codesUri, codesVersion);
            koodistoBusinessService.delete(codesUri, codesVersion);
            return ResponseEntity.status(202).body(null);
      }
}
