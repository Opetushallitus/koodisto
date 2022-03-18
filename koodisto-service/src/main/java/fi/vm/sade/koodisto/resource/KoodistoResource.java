package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController()
@RequestMapping({"/json"})
//@Api(value = "/rest/json", description = "REST/JSON rajapinta")
public class KoodistoResource {

    /*private static final String KUVAUS = "kuvaus";
    private static final String NIMI = "nimi";
    private static final String LANG = "lang";
    private static final String KOODI_VERSIO_SELECTION = "koodiVersioSelection";
    private static final String VALID_AT = "validAt";
    private static final String KOODI_TILAS = "koodiTilas";
    private static final String KOODI_URIS = "koodiUris";
    private static final String KOODI_VERSIO = "koodiVersio";
    private static final String ONLY_VALID_KOODIS = "onlyValidKoodis";
    private static final String KOODI_ARVO = "koodiArvo";
    private static final String KOODI_URI = "koodiUri";
    private static final String KOODISTO_VERSIO = "koodistoVersio";
    private static final String KOODISTO_URI = "koodistoUri";
    public static final int ONE_HOUR = 60 * 60;*/

    private final DatatypeFactory datatypeFactory;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoConversionService conversionService;


    public KoodistoResource() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Listaa kaikki koodistoryhmät",
            notes = "Palauttaa kaikki koodistoryhmät ja niiden sisältämät koodistot",
            response = KoodistoRyhmaListDto.class,
            responseContainer = "List")*/
    public List<KoodistoRyhmaListDto> listAllKoodistoRyhmas() {
        return conversionService.convertAll(koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
    }

    @GetMapping(path= "/{koodistoUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Transactional // TODO FIXME?
    /*@ApiOperation(
            value = "Lataa koodisto URIn perusteella",
            notes = "Palautaa koodiston, jonka URI on {koodistouri}. Versionumeron voi antaa URL-parametrina",
            response = KoodistoDto.class)*/
    public KoodistoDto getKoodistoByUri(
            @PathVariable String koodistoUri,
            @RequestParam() Integer koodistoVersio) {

        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        return conversionService.convert(koodisto, KoodistoDto.class);
    }

    @GetMapping(path = "/{koodistoUri}/koodi", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    @Transactional
    /*@ApiOperation(
            value = "Listaa koodiston kaikki koodit",
            notes = "Palauttaa koodiston, jonka URI on {koodistouri} koodit. Koodiston versionumeron voi antaa URL-parametrina",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public List<KoodiDto> getKoodisByKoodisto(
            @PathVariable String koodistoUri,
            @RequestParam Integer koodistoVersio,
            @RequestParam(defaultValue = "false") boolean onlyValidKoodis) {

        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodisto(koodistoUri, onlyValidKoodis);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersio(koodistoUri, koodistoVersio, onlyValidKoodis);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @GetMapping(path = "/{koodistoUri}/koodi/arvo/{koodiArvo}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Listaa koodit arvon perusteella",
            notes = "Palauttaa koodistosta, jonka URI on {koodistouri}, koodin, jonka arvo on {koodiArvo}. Koodiston ja koodin versionumerot voi antaa URL-parametreina. Nykyisellä toteutuksella palauttaa vain yhden koodin.",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public List<KoodiDto> getKoodisByArvo(
            @PathVariable String koodistoUri,
            @PathVariable String koodiArvo,
            @RequestParam Integer koodistoVersio) {
        // TODO: tämän pitäisi palauttaa vain yksi koodi
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodistoWithKoodiArvo(koodistoUri, koodiArvo);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersioWithKoodiArvo(koodistoUri, koodistoVersio, koodiArvo);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @GetMapping(path = "/{koodistoUri}/koodi/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Lataa koodi URIn perusteella",
            notes = "Palauttaa koodistosto, jonka URI on {koodistouri}, koodin, jonka URI on {koodiuri}. Koodiston ja koodin versionumerot voi antaa URL-parametreina",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public KoodiDto getKoodiByUri(
            @PathVariable String koodistoUri,
            @PathVariable String koodiUri,
            @RequestParam Integer koodistoVersio) {
        KoodiVersioWithKoodistoItem koodi;
        if (koodistoVersio == null) {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } else {
            koodi = koodiBusinessService.getKoodiByKoodistoVersio(koodistoUri, koodistoVersio, koodiUri);
        }

        return conversionService.convert(koodi, KoodiDto.class);
    }

    @GetMapping(path = "/relaatio/sisaltyy-alakoodit/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Listaa koodin sisältämät koodit",
            notes = "Palauttaa koodille, jonka URI on {koodiuri}, sisältyvyyssuhteessa alapuolelle määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.",
            response = KoodiDto.class, responseContainer = "List")*/
    public List<KoodiDto> getAlakoodis(
            @PathVariable String koodiUri,
            @RequestParam Integer koodiVersio) {
        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GetMapping(path = "/relaatio/sisaltyy-ylakoodit/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    /*@Cacheable(maxAgeSeconds = ONE_HOUR)
    @ApiOperation(
            value = "Listaa koodit, joihin koodi sisältyy",
            notes = "Palauttaa koodille, jonka URI on {koodiuri}, sisältyvyyssuhteessa yläpuolelle määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public List<KoodiDto> getYlakoodis(
            @PathVariable String koodiUri,
            @RequestParam Integer koodiVersio) {
        final boolean isChild = true;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @GetMapping(path = "/relaatio/rinnasteinen/{koodiUri}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Listaa koodiin rinnastuvat koodit",
            notes = "Palauttaa koodille, jonka URI on {koodiuri}, rinnastuvuussuhteessa määritellyt koodit. Koodin versionumeron voi antaa URL-parametrina.",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public List<KoodiDto> getRinnasteinenKoodis(
            @PathVariable String koodiUri,
            @RequestParam Integer koodiVersio
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
    @GetMapping(path = "/searchKoodis", produces = MediaType.APPLICATION_JSON_VALUE)
    //@Cacheable(maxAgeSeconds = ONE_HOUR)
    /*@ApiOperation(
            value = "Listaa koodit hakukriteerien perusteella",
            notes = "Haun rajaamiseksi ainakin jokin seuraavista parametreistä pitää olla annettu: koodiUris, koodiArvo, validAt",
            response = KoodiDto.class,
            responseContainer = "List")*/
    public List<KoodiDto> searchKoodis(
            @RequestParam List<String> koodiUris,
            @RequestParam String koodiArvo,
            @RequestParam List<TilaType> koodiTilas,
            @RequestParam String validAtDate,
            @RequestParam Integer koodiVersio,
            @RequestParam SearchKoodisVersioSelectionType koodiVersioSelection
            ) throws IllegalAccessException, NoSuchMethodException,
                    InvocationTargetException, ParseException {

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
            //GregorianCalendar cal = new GregorianCalendar();
            //cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(validAtDate));
            searchCriteria.setValidAt(new SimpleDateFormat("yyyy-MM-dd").parse(validAtDate));
        }

        List<KoodiVersioWithKoodistoItem> koodis = koodiBusinessService.searchKoodis(searchCriteria);
        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @GetMapping(path = "/{koodistoUri}.properties", produces = MediaType.TEXT_PLAIN_VALUE)
    /*@Cacheable(maxAgeSeconds = ONE_HOUR)
    @ApiOperation(
            value = "Hae koodiston tiedot suomeksi",
            notes = "Sisältää listan koodiston koodiarvoista ja niiden nimistä.",
            response = String.class)*/
    public String getKoodistoAsPropertiesDefaultLang(
            @PathVariable String koodistoUri
            ) throws IOException {
        return getKoodistoAsProperties(koodistoUri, "FI");
    }

    @GetMapping(path = "/{koodistoUri}_{lang}.properties", produces = MediaType.TEXT_PLAIN_VALUE)
    /*@Cacheable(maxAgeSeconds = ONE_HOUR)
    @ApiOperation(
            value = "Hae koodiston tiedot kielen perusteella",
            notes = "Sisältää listan koodiston koodiarvoista ja niiden nimistä.",
            response = String.class)*/
    public String getKoodistoAsProperties(
            @PathVariable String koodistoUri,
            @PathVariable String lang
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

    @PostMapping(path = "/{koodistoUri}/koodi/{koodiUri}/kieli/{lang}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    /*@ApiOperation(
            value = "Päivitä tai luo koodin kielitiedot",
            notes = "Jos koodilla on jo nimi ja kuvaus annetulla kielellä, ne päivitetään. Muussa tapauksessa koodille luodaan kyseiset kielitiedot.",
            response = KoodiDto.class)*/
    public KoodiDto updateKoodiLangMetaData(
            @PathVariable String koodistoUri,
            @PathVariable String koodiUri,
            @PathVariable String lang,
            @RequestParam String nimi, // TODO requestparam vs formparam
            @RequestParam String kuvaus
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
