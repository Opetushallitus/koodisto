package fi.vm.sade.koodisto.resource;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.FieldLengths;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController()
@RequestMapping({"/rest/json"})
public class KoodistoResource {

    private final KoodistoBusinessService koodistoBusinessService;

    private final KoodiBusinessService koodiBusinessService;

    private final KoodistoConversionService conversionService;

    public KoodistoResource(KoodistoBusinessService koodistoBusinessService, KoodiBusinessService koodiBusinessService, KoodistoConversionService conversionService) {
        this.koodistoBusinessService = koodistoBusinessService;
        this.koodiBusinessService = koodiBusinessService;
        this.conversionService = conversionService;
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Listaa kaikki koodistoryhmät",
            summary = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot")
    public List<KoodistoRyhmaListDto> listAllKoodistoRyhmas() {
        return conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/{koodistoUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Lataa koodisto URIn perusteella",
            summary = "Palautaa koodiston, jonka URI on {koodistouri}. Versionumeron voi antaa URL-parametrina")
    public KoodistoDto getKoodistoByUri(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Koodiston Versio")  @RequestParam(required = false) Integer koodistoVersio) {

        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        return conversionService.convert(koodisto, KoodistoDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/{koodistoUri}/koodi", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Listaa koodiston kaikki koodit",
            summary = "Palauttaa koodiston, jonka URI on {koodistouri} koodit. Koodiston versionumeron voi antaa URL-parametrina")
    public List<KoodiDto> getKoodisByKoodisto(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Koodiston Versio") @RequestParam(required = false) Integer koodistoVersio,
            @Parameter(description = "Palauta vain voimassa olevia koodeja") @RequestParam(defaultValue = "false") boolean onlyValidKoodis) {

        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodisto(koodistoUri, onlyValidKoodis);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersio(koodistoUri, koodistoVersio, onlyValidKoodis);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/{koodistoUri}/koodi/arvo/{koodiArvo}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Listaa koodit arvon perusteella",
            summary = "Palauttaa koodistosta, jonka URI on {koodistouri}, koodin, jonka arvo on {koodiArvo}. Koodiston ja koodin versionumerot voi antaa URL-parametreina. Nykyisellä toteutuksella palauttaa vain yhden koodin.")
    public List<KoodiDto> getKoodisByArvo(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Koodiston Arvo") @PathVariable String koodiArvo,
            @Parameter(description = "Koodiston Versio") @RequestParam(required = false) Integer koodistoVersio) {
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodistoWithKoodiArvo(koodistoUri, koodiArvo);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersioWithKoodiArvo(koodistoUri, koodistoVersio, koodiArvo);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/{koodistoUri}/koodi/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(
            description = "Lataa koodi URIn perusteella",
            summary = "Palauttaa koodistosto, jonka URI on {koodistouri}, koodin, jonka URI on {koodiuri}. Koodiston ja koodin versionumerot voi antaa URL-parametreina")
    public KoodiDto getKoodiByUri(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Koodin URI") @PathVariable String koodiUri,
            @Parameter(description = "Koodiston Versio") @RequestParam(required = false) Integer koodistoVersio) {
        KoodiVersioWithKoodistoItem koodi;
        if (koodistoVersio == null) {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } else {
            koodi = koodiBusinessService.getKoodiByKoodistoVersio(koodistoUri, koodistoVersio, koodiUri);
        }

        return conversionService.convert(koodi, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/relaatio/sisaltyy-alakoodit/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(
            description = "Listaa koodin sisältämät koodit",
            summary = "Palauttaa koodille, jonka URI on {koodiuri}, sisältyvyyssuhteessa alapuolelle määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.")
    public List<KoodiDto> getAlakoodis(
            @Parameter(description = "Koodin URI") @PathVariable String koodiUri,
            @Parameter(description = "Koodiston Versio") @RequestParam(required = false) Integer koodiVersio) {
        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/relaatio/sisaltyy-ylakoodit/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(
            description = "Listaa koodit, joihin koodi sisältyy",
            summary = "Palauttaa koodille, jonka URI on {koodiuri}, sisältyvyyssuhteessa yläpuolelle määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.")
    public List<KoodiDto> getYlakoodis(
            @Parameter(description = "Koodin URI") @PathVariable String koodiUri,
            @Parameter(description = "Koodin Versio") @RequestParam(required = false) Integer koodiVersio) {
        final boolean isChild = true;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/relaatio/rinnasteinen/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Listaa koodiin rinnastuvat koodit",
            summary = "Palauttaa koodille, jonka URI on {koodiuri}, rinnastuvuussuhteessa määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.")
    public List<KoodiDto> getRinnasteinenKoodis(
            @Parameter(description = "Koodin URI") @PathVariable String koodiUri,
            @Parameter(description = "Koodin Versio") @RequestParam(required = false) Integer koodiVersio
    ) {
        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.RINNASTEINEN;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    protected List<KoodiDto> getRelations(String koodiUri, Integer koodiVersio, SuhteenTyyppi suhteenTyyppi, boolean isChild) {
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodiVersio == null) {
            koodis = koodiBusinessService.listByRelation(koodiUri, isChild, suhteenTyyppi);
        } else {
            koodis = koodiBusinessService.listByRelation(koodiUri, koodiVersio, isChild, suhteenTyyppi);
        }
        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    /**
     * http://localhost:5050/koodisto-service/rest/json/searchKoodis?koodiUris=kunta&koodiTilas=LUONNOS&validAt=2013-06-06&koodiVersioSelection=ALL
     */
    @JsonView(JsonViews.Basic.class)
    @GetMapping(path = "/searchKoodis", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(
            description = "Listaa koodit hakukriteerien perusteella",
            summary = "Haun rajaamiseksi ainakin jokin seuraavista parametreistä pitää olla annettu: koodiUris, koodiArvo, validAt")
    public List<KoodiDto> searchKoodis(
            @Parameter(description = "Lista koodi URI:ja") @RequestParam(required = false) List<String> koodiUris,
            @Parameter(description = "Koodin arvo") @RequestParam(required = false) String koodiArvo,
            @Parameter(description = "Koodin tila: PASIIVINEN, LUONNOS, HYVAKSYTTY") @RequestParam(required = false) List<TilaType> koodiTilas,
            @Parameter(description = "Päiväys, jolloin koodi on voimassa") @RequestParam(required = false) String validAtDate,
            @Parameter(description = "Koodin versio, ainoastaan, jos version valinta on SPECIFIC") @RequestParam(required = false) Integer koodiVersio,
            @Parameter(description = "Koodin version valinta: ALL, LATEST, SPECIFIC") @RequestParam(required = false) SearchKoodisVersioSelectionType koodiVersioSelection
    ) throws ParseException {

        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();

        searchCriteria.setKoodiArvo(koodiArvo);
        searchCriteria.setKoodiVersio(koodiVersio);
        searchCriteria.setKoodiVersioSelection(koodiVersioSelection);
        if (koodiUris != null) {
            searchCriteria.getKoodiUris().addAll(koodiUris);
        }
        if (koodiTilas != null) {
            searchCriteria.getKoodiTilas().addAll(koodiTilas);
        }
        if (validAtDate != null) {
            searchCriteria.setValidAt(new SimpleDateFormat("yyyy-MM-dd").parse(validAtDate));
        }

        List<KoodiVersioWithKoodistoItem> koodis = koodiBusinessService.searchKoodis(searchCriteria);
        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @GetMapping(path = "/{koodistoUri}.properties", produces = MediaType.TEXT_PLAIN_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Hae koodiston tiedot suomeksi",
            summary = "Sisältää listan koodiston koodiarvoista ja niiden nimistä.")
    public String getKoodistoAsPropertiesDefaultLang(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri
    ) throws IOException {
        return getKoodistoAsProperties(koodistoUri, "FI");
    }

    @GetMapping(path = "/{koodistoUri}_{lang}.properties", produces = MediaType.TEXT_PLAIN_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Operation(description = "Hae koodiston tiedot kielen perusteella",
            summary = "Sisältää listan koodiston koodiarvoista ja niiden nimistä.")
    public String getKoodistoAsProperties(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Kieli (FI, SV tai EN)") @PathVariable String lang
    ) throws IOException {
        List<KoodiVersioWithKoodistoItem> koodis = koodiBusinessService.getKoodisByKoodisto(koodistoUri, false);
        Properties props = new Properties();
        // iterate all koodis
        for (KoodiVersioWithKoodistoItem koodi : koodis) {
            // get correct lang metadata for koodi
            KoodiMetadata langMetadata = null;
            for (KoodiMetadata metadata : koodi.getKoodiVersio().getMetadatas()) {
                if (metadata.getKieli().toString().equalsIgnoreCase(lang)) {
                    langMetadata = metadata;
                }
            }
            // add koodi nimi data to properties
            props.put(koodi.getKoodiVersio().getKoodi().getKoodiUri(), langMetadata != null ? langMetadata.getNimi() : "");
        }
        // print out properties
        StringWriter sw = new StringWriter();
        props.store(new PrintWriter(sw), "");
        return sw.toString();
    }

    @JsonView(JsonViews.Basic.class)
    @PostMapping(path = "/{koodistoUri}/koodi/{koodiUri}/kieli/{lang}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole(T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_READ_UPDATE,T(fi.vm.sade.koodisto.util.KoodistoRole).ROLE_APP_KOODISTO_CRUD)")
    @Operation(description = "Päivitä tai luo koodin kielitiedot",
            summary = "Jos koodilla on jo nimi ja kuvaus annetulla kielellä, ne päivitetään. Muussa tapauksessa koodille luodaan kyseiset kielitiedot.")
    public KoodiDto updateKoodiLangMetaData(
            @Parameter(description = "Koodiston URI") @PathVariable String koodistoUri,
            @Parameter(description = "Koodin URI") @PathVariable String koodiUri,
            @Parameter(description = "Kieli (FI, SV tai EN)") @PathVariable String lang,
            @Parameter(description = "Koodin nimi") @RequestParam String nimi,
            @Parameter(description = "Koodin kuvaus") @RequestParam String kuvaus
    ) {
        nimi = nimi.substring(0, Math.min(nimi.length(), FieldLengths.DEFAULT_FIELD_LENGTH)); // nimi cannot be longer

        // find/create koodi
        KoodiVersioWithKoodistoItem koodi;
        try {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } catch (KoodiNotFoundException e) {
            // if koodi not exists -> try to create it with default ml nimi data
            CreateKoodiDataType createKoodiData = new CreateKoodiDataType();
            createKoodiData.setVoimassaAlkuPvm(new Date());
            createKoodiData.setKoodiArvo(koodiUri);
            createKoodiData.getMetadata().add(createKoodiMetadata("fi", koodiUri, koodiUri));
            createKoodiData.getMetadata().add(createKoodiMetadata("sv", koodiUri, koodiUri));
            createKoodiData.getMetadata().add(createKoodiMetadata("en", koodiUri, koodiUri));
            koodi = koodiBusinessService.createKoodi(koodistoUri, createKoodiData);
        }
        // convert
        KoodiType koodiType = conversionService.convert(koodi, KoodiType.class);
        // prepare update data
        UpdateKoodiDataType updateKoodiData = new UpdateKoodiDataType();
        KoodistoHelper.copyFields(koodiType, updateKoodiData);

        // dig out lang metadata
        KoodiMetadataType langMetadata = null;
        for (KoodiMetadataType metadata : updateKoodiData.getMetadata()) {
            if (metadata.getKieli().toString().equalsIgnoreCase(lang)) {
                langMetadata = metadata;
            }
        }
        // create lang metadata if it doesn't yet exist
        if (langMetadata == null) {
            langMetadata = createKoodiMetadata(lang, nimi, kuvaus);
            updateKoodiData.getMetadata().add(langMetadata);
        }
        // set new nimi for specified lang
        langMetadata.setNimi(nimi);
        langMetadata.setKuvaus(kuvaus);

        // update koodi
        koodi = koodiBusinessService.updateKoodi(updateKoodiData);
        return conversionService.convert(koodi, KoodiDto.class);
    }

    private KoodiMetadataType createKoodiMetadata(String lang, String nimi, String kuvaus) {
        KoodiMetadataType metadata;
        metadata = new KoodiMetadataType();
        metadata.setKieli(KieliType.fromValue(lang.toUpperCase()));
        metadata.setNimi(nimi);
        metadata.setKuvaus(kuvaus);
        metadata.setLyhytNimi(nimi.substring(0, Math.min(nimi.length(), FieldLengths.DEFAULT_FIELD_LENGTH))); // lyhytniminimi cannot be longer
        return metadata;
    }

}
