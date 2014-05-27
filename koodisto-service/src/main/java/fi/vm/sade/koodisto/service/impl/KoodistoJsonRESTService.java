
package fi.vm.sade.koodisto.service.impl;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import fi.vm.sade.generic.rest.Cacheable;
import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.common.util.FieldLengths;
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
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.UpdateKoodiDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import com.wordnik.swagger.annotations.*;

/**
 * User: wuoti
 * Date: 21.5.2013
 * Time: 9.02
 */
@Component
@Path("/json")
@Api(value = "/json", description = "JSON rajapinta")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class KoodistoJsonRESTService {

    private static final String KUVAUS = "kuvaus";
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
    public static final int ONE_HOUR = 60 * 60;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private SadeConversionService conversionService;

    @JsonView(JsonViews.Basic.class)
    @GET
    @ApiOperation(value = "Find pet by ID", notes = "More notes about this method", response = KoodistoRyhmaListDto.class)
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodistoRyhmaListDto> listAllKoodistoRyhmas() {
        return conversionService.convertAll(
                koodistoBusinessService.listAllKoodistoRyhmas(), KoodistoRyhmaListDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/{koodistoUri}")
    @ApiOperation(value = "Get koodisto", notes = "Hephei! kuvaus!", response = KoodistoDto.class)
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    @Transactional
    public KoodistoDto getKoodistoByUri(@PathParam(KOODISTO_URI) String koodistoUri,
                                        @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {

        KoodistoVersio koodisto = null;
        if (koodistoVersio == null) {
            koodisto = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        } else {
            koodisto = koodistoBusinessService.getKoodistoVersio(koodistoUri, koodistoVersio);
        }

        return conversionService.convert(koodisto, KoodistoDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/{koodistoUri}/koodi")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    @Transactional
    public List<KoodiDto> getKoodisByKoodisto(@PathParam(KOODISTO_URI) String koodistoUri,
                                              @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio,
                                              @QueryParam(ONLY_VALID_KOODIS) @DefaultValue("false") boolean onlyValidKoodis) {

        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodisto(koodistoUri, onlyValidKoodis);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersio(koodistoUri, koodistoVersio, onlyValidKoodis);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/{koodistoUri}/koodi/arvo/{koodiArvo}")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodiDto> getKoodisByArvo(@PathParam(KOODISTO_URI) String koodistoUri,
                                          @PathParam(KOODI_ARVO) String koodiArvo,
                                          @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        List<KoodiVersioWithKoodistoItem> koodis = null;
        if (koodistoVersio == null) {
            koodis = koodiBusinessService.getKoodisByKoodistoWithKoodiArvo(koodistoUri, koodiArvo);
        } else {
            koodis = koodiBusinessService.getKoodisByKoodistoVersioWithKoodiArvo(koodistoUri, koodistoVersio, koodiArvo);
        }

        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/{koodistoUri}/koodi/{koodiUri}")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public KoodiDto getKoodiByUri(@PathParam(KOODISTO_URI) String koodistoUri,
                                  @PathParam(KOODI_URI) String koodiUri,
                                  @QueryParam(KOODISTO_VERSIO) Integer koodistoVersio) {
        KoodiVersioWithKoodistoItem koodi;
        if (koodistoVersio == null) {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } else {
            koodi = koodiBusinessService.getKoodiByKoodistoVersio(koodistoUri, koodistoVersio, koodiUri);
        }

        return conversionService.convert(koodi, KoodiDto.class);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/relaatio/sisaltyy-alakoodit/{koodiUri}")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodiDto> getAlakoodis(@PathParam(KOODI_URI) String koodiUri,
                                       @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/relaatio/sisaltyy-ylakoodit/{koodiUri}")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodiDto> getYlakoodis(@PathParam(KOODI_URI) String koodiUri,
                                       @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = true;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.SISALTYY;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    @JsonView(JsonViews.Basic.class)
    @GET
    @Path("/relaatio/rinnasteinen/{koodiUri}")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodiDto> getRinnasteinenKoodis(@PathParam(KOODI_URI) String koodiUri,
                                                @QueryParam(KOODI_VERSIO) Integer koodiVersio) {

        final boolean isChild = false;
        final SuhteenTyyppi suhteenTyyppi = SuhteenTyyppi.RINNASTEINEN;

        return getRelations(koodiUri, koodiVersio, suhteenTyyppi, isChild);
    }

    protected List<KoodiDto> getRelations(String koodiUri, Integer koodiVersio,
                                          SuhteenTyyppi suhteenTyyppi, boolean isChild) {
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
    @GET
    @Path("/searchKoodis")
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public List<KoodiDto> searchKoodis(
            @QueryParam(KOODI_URIS) List<String> koodiUris,
            @QueryParam(KOODI_ARVO) String koodiArvo,
            @QueryParam(KOODI_TILAS) List<TilaType> koodiTilas,
            @QueryParam(VALID_AT) String validAtDate,
            @QueryParam(KOODI_VERSIO) Integer koodiVersio,
            @QueryParam(KOODI_VERSIO_SELECTION) SearchKoodisVersioSelectionType koodiVersioSelection
    ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException {

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
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(validAtDate));
            searchCriteria.setValidAt(new XMLGregorianCalendarImpl(cal));
        }

        List<KoodiVersioWithKoodistoItem> koodis = koodiBusinessService.searchKoodis(searchCriteria);
        return conversionService.convertAll(koodis, KoodiDto.class);
    }

    @GET
    @Path("/{koodistoUri}.properties")
    @Produces(value = MediaType.TEXT_PLAIN)
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public String getKoodistoAsPropertiesDefaultLang(@PathParam(KOODISTO_URI) String koodistoUri) throws IOException {
        return getKoodistoAsProperties(koodistoUri, "FI");
    }

    @GET
    @Path("/{koodistoUri}_{lang}.properties")
    @Produces(value = MediaType.TEXT_PLAIN)
    @Cacheable(maxAgeSeconds = ONE_HOUR)
    public String getKoodistoAsProperties(@PathParam(KOODISTO_URI) String koodistoUri, @PathParam(LANG) String lang) throws IOException {
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
    @POST
    @Path("/{koodistoUri}/koodi/{koodiUri}/kieli/{lang}/metadata")
    @PreAuthorize("hasAnyRole('ROLE_APP_KOODISTO_READ_UPDATE','ROLE_APP_KOODISTO_CRUD')")
    public KoodiDto updateKoodiLangMetaData(
            @PathParam(KOODISTO_URI) String koodistoUri,
            @PathParam(KOODI_URI) String koodiUri,
            @PathParam(LANG) String lang,
            @FormParam(NIMI) String nimi,
            @FormParam(KUVAUS) String kuvaus
    ) {
        nimi = nimi.substring(0, Math.min(nimi.length(), FieldLengths.DEFAULT_FIELD_LENGTH)); // nimi cannot be longer

        // find/create koodi
        KoodiVersioWithKoodistoItem koodi;
        try {
            koodi = koodiBusinessService.getKoodiByKoodisto(koodistoUri, koodiUri);
        } catch (KoodiNotFoundException e) {
            // if koodi not exists -> try to create it with default ml nimi data
            CreateKoodiDataType createKoodiData = new CreateKoodiDataType();
            createKoodiData.setVoimassaAlkuPvm(new XMLGregorianCalendarImpl(new GregorianCalendar()));
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
