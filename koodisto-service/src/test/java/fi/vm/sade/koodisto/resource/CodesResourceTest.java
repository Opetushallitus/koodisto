package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/truncate_tables.sql")
@Sql("/test-data-codes-rest.sql")
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class CodesResourceTest {
    private final String BASE_PATH = "/rest/codes";

    @Autowired
    private ResourceHelper helper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsWithinRelationBetweenExistingcodes() throws Exception {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));

        mockMvc.perform(post(BASE_PATH + "/addrelation/{codesUri}/{codesUriToAdd}/{relationType}", parentUri, childUri, SuhteenTyyppi.SISALTYY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[{\"codesUri\":\"eisuhteitaviela2\",\"codesVersion\":1}]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"levelsWithCodes\":[],\"withinCodes\":[{\"codesUri\":\"eisuhteitaviela1\",\"codesVersion\":1}],\"includesCodes\":[]}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsLevesWithRelationBetweenExistingcodes() throws Exception {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));

        mockMvc.perform(post(BASE_PATH + "/addrelation/{codesUri}/{codesUriToAdd}/{relationType}", parentUri, childUri, SuhteenTyyppi.RINNASTEINEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"includesCodes\":[],\"withinCodes\":[],\"levelsWithCodes\":[{\"codesUri\":\"eisuhteitaviela2\",\"codesVersion\":1}]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"withinCodes\":[],\"levelsWithCodes\":[{\"codesUri\":\"eisuhteitaviela1\",\"codesVersion\":1}],\"includesCodes\":[]}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void removesRelationBetweenExistingcodes() throws Exception {
        String parentUri = "sisaltyysuhde1";
        String childUri = "sisaltyysuhde2";
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[{\"codesUri\":\"sisaltyysuhde2\",\"codesVersion\":1}]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[{\"codesUri\":\"sisaltyysuhde1\",\"codesVersion\":1}],\"includesCodes\":[]}"));

        mockMvc.perform(post(BASE_PATH + "/removerelation/{codesUri}/{codesUriToRemove}/{relationType}", parentUri, childUri, "SISALTYY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", parentUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", childUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"levelsWithCodes\":[],\"withinCodes\":[],\"includesCodes\":[]}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void insertsNewCodes() throws Exception {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";
        String codesToBeInserted = createKoodistoString(koodistoUri, codesGroupUri);
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codesToBeInserted))
                .andExpect(status().isCreated());
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{\"codesGroupUri\":\"%s\",\"koodistoUri\":\"%s\",\"versio\": 1}", codesGroupUri, koodistoUri)));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void invalidInsertCausesError() throws Exception {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";

        String codesToBeInserted = createKoodistoString(koodistoUri, codesGroupUri);
        codesToBeInserted = codesToBeInserted.replaceAll("\"kieli\":\"FI\"", "\"kieli\":null");
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codesToBeInserted))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.validation.language"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void uudenVersionTallennusSailyttaaVanhanVersionEnnallaan() {
        // uusi koodisto
        LocalDate alkuPvmV1 = LocalDate.of(2000, 1, 1);
        LocalDate loppuPvmV1 = alkuPvmV1.with(lastDayOfYear());
        KoodistoDto koodistoV1a = helper.createKoodisto(newKoodistoDto(
                "koodistojenlisaaminenkoodistoryhmaan", "organisaatio1",
                "test", "koodistoV1",
                alkuPvmV1, loppuPvmV1));
        assertThat(koodistoV1a)
                .returns(1, KoodistoDto::getVersio)
                .returns(Tila.LUONNOS, KoodistoDto::getTila);
        KoodiDto koodiV1a = helper.createKoodi(koodistoV1a.getKoodistoUri(),
                newKoodiDto(koodistoV1a, "koodi1", "koodiV1"));

        // lisätään koodille voimassaoloaika
        koodiV1a.setVoimassaLoppuPvm(java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
        KoodiDto koodiV1b = helper.updateKoodi(koodiV1a);
        assertThat(koodiV1b)
                .returns(koodiV1a.getVersio(), KoodiDto::getVersio)
                .returns(Tila.LUONNOS, KoodiDto::getTila);

        // hyväksytään ensimmäinen versio
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1a.getVersio());
        koodistoV1a.setTila(Tila.HYVAKSYTTY);
        KoodistoDto koodistoV1b = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV1b)
                .returns(koodistoV1a.getVersio(), KoodistoDto::getVersio)
                .returns(Tila.HYVAKSYTTY, KoodistoDto::getTila);
        ExtendedKoodiDto koodiV1c = helper.getKoodi(koodiV1a.getKoodiUri(), koodiV1a.getVersio());
        assertThat(koodiV1c)
                .returns(koodiV1a.getVersio(), ExtendedKoodiDto::getVersio)
                .returns(Tila.HYVAKSYTTY, ExtendedKoodiDto::getTila);

        // muokataan koodistoa, pitäisi tulla uusi versio
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1b.getVersio());
        koodistoV1a.getMetadata().get(0).setNimi("koodistoV2");
        KoodistoDto koodistoV2a = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV2a)
                .returns(2, KoodistoDto::getVersio)
                .returns(Tila.LUONNOS, KoodistoDto::getTila);
        ExtendedKoodiDto koodiV2a = helper.getKoodi(koodiV1c.getKoodiUri(), koodiV1c.getVersio() + 1);
        assertThat(koodiV2a)
                .returns(2, ExtendedKoodiDto::getVersio)
                .returns(Tila.LUONNOS, ExtendedKoodiDto::getTila);

        // hyväksytään uusi versio, versio pitäisi pysyä samana
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV2a.getVersio());
        LocalDate alkuPvmV2 = alkuPvmV1.plusYears(1);
        LocalDate loppuPvmV2 = alkuPvmV2.with(lastDayOfYear());
        koodistoV1a.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvmV2));
        koodistoV1a.setVoimassaLoppuPvm(java.sql.Date.valueOf(loppuPvmV2));
        koodistoV1a.setTila(Tila.HYVAKSYTTY);
        KoodistoDto koodistoV2b = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV2b)
                .returns(koodistoV2a.getVersio(), KoodistoDto::getVersio)
                .returns(Tila.HYVAKSYTTY, KoodistoDto::getTila)
                .returns(java.sql.Date.valueOf(alkuPvmV2), KoodistoDto::getVoimassaAlkuPvm)
                .returns(java.sql.Date.valueOf(loppuPvmV2), KoodistoDto::getVoimassaLoppuPvm);

        // tarkastetaan että v1 on vielä kunnossa
        KoodistoDto koodistoV1c = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1b.getVersio());
        assertThat(koodistoV1c).isEqualToIgnoringGivenFields(koodistoV1b, "codesVersions");
        ExtendedKoodiDto koodiV1d = helper.getKoodi(koodiV1a.getKoodiUri(), koodiV1b.getVersio());
        assertThat(koodiV1d).isEqualToIgnoringGivenFields(koodiV1b,
                "version", "koodisto", "paivitysPvm", "tila",
                "withinCodeElements", "includesCodeElements", "levelsWithCodeElements", "metadata");
    }

    @Test
    void listsCodesByCodesUri() throws Exception {
        String koodistoUri = "moniaversioita";
        mockMvc.perform(get(BASE_PATH + "/{codesUri}", koodistoUri))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"koodistoVersios\": [{\"versio\": 1},{\"versio\": 2},{\"versio\": 3}]}"));
    }

    @Test
    void getLatestCodeByUri() throws Exception {
        String koodistoUri = "moniaversioita";
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 0))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"versio\": 3}"));
    }

    @Test
    void listCodes() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":-99,\"koodistoRyhmaUri\":\"dummy\",\"metadata\":[{\"nimi\":\"Dummy\",\"kieli\":\"FI\"}],\"koodistos\":[{\"koodistoUri\":\"dummy\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Dummy\"}]}}]},{\"id\":-6,\"koodistoRyhmaUri\":\"montametadataa\",\"metadata\":[{\"nimi\":\"Koodistoryhmä jolla monta metadataa\",\"kieli\":\"FI\"},{\"nimi\":\"Koodistoryhmä jolla monta metadataa\",\"kieli\":\"SV\"},{\"nimi\":\"Koodistoryhmä jolla monta metadataa\",\"kieli\":\"EN\"}],\"koodistos\":[]},{\"id\":-5,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen2\",\"metadata\":[{\"nimi\":\"Koodistoryhmän päivittäminen\",\"kieli\":\"FI\"}],\"koodistos\":[{\"koodistoUri\":\"koodistoryhmatestikoodisto\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Update koodistoryhmä testi\"}]}}]},{\"id\":-4,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen\",\"metadata\":[{\"nimi\":\"Tyhjän koodistoryhmän päivittäminen\",\"kieli\":\"FI\"}],\"koodistos\":[]},{\"id\":-3,\"koodistoRyhmaUri\":\"koodistoryhmantuhoaminen\",\"metadata\":[{\"nimi\":\"Tyhjän koodistoryhmän tuhoaminen\",\"kieli\":\"FI\"}],\"koodistos\":[]},{\"id\":-2,\"koodistoRyhmaUri\":\"koodistojenlisaaminenkoodistoryhmaan\",\"metadata\":[{\"nimi\":\"Koodistojen lisääminen koodistoryhmään\",\"kieli\":\"FI\"}],\"koodistos\":[]},{\"id\":-1,\"koodistoRyhmaUri\":\"relaatioidenlisaaminen\",\"metadata\":[{\"nimi\":\"Relaatioiden lisääminen\",\"kieli\":\"FI\"}],\"koodistos\":[{\"koodistoUri\":\"paljonmuutoksia\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":3,\"voimassaAlkuPvm\":\"2013-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Paljon muutettu\"},{\"kieli\":\"EN\",\"nimi\":\"Plenty of changes\"}]}},{\"koodistoUri\":\"eisuhteitaviela1\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"1 sisaltaa 2 -testi\"}]}},{\"koodistoUri\":\"eisuhteitaviela2\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"2 sisältyy 1 -testi\"}]}},{\"koodistoUri\":\"eisuhteitaviela3\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"3 rinnastuu 4 -testi\"}]}},{\"koodistoUri\":\"eisuhteitaviela4\",\"organisaatioOid\":\"1.2.2004.6\",\"latestKoodistoVersio\":{\"versio\":1,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"4 rinnastuu 3 -testi\"}]}}]}]"));
        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Dummy\",\"kuvaus\":\"kuvaus\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":3,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2013-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"LUONNOS\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Paljon muutettu\",\"kuvaus\":\"Muutettu vähän kaikkea\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null},{\"kieli\":\"EN\",\"nimi\":\"Plenty of changes\",\"kuvaus\":\"A lot of changes in this codes\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Koodistoryhmään lisättävä koodi\",\"kuvaus\":\"Lisätään koodiryhmään urin ja organisaation perusteella\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Update koodistoryhmä testi\",\"kuvaus\":\"Tämä testaa täyden koodistoryhmän päivittämistä\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"PASSIIVINEN\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Delete testi\",\"kuvaus\":\"Tämä tuhotaan.\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"CSV download testi\",\"kuvaus\":\"Tämä ladataan csv-download testissä.\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Update testi\",\"kuvaus\":\"Tämä päivitetään uudella nimellä\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"XLS upload testi\",\"kuvaus\":\"Tänne ladataan excel_example.xml\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"XML upload testi\",\"kuvaus\":\"Tänne ladataan jhs_xml_example.xml\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"CSV upload testi\",\"kuvaus\":\"Tänne ladataan csv_example.csv\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Listattava koodisto\",\"kuvaus\":\"Täytettä koodistoryhmä 3:een\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":3,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"7n versio3\",\"kuvaus\":\"Versio3\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"6 sisältyy 5\",\"kuvaus\":\"6 sisältyy 5\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"5 sisältää 6\",\"kuvaus\":\"5 sisältää 6\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"4 rinnastuu 3 -testi\",\"kuvaus\":\"4 rinnastuu 3 -testi\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"3 rinnastuu 4 -testi\",\"kuvaus\":\"3 rinnastuu 4 -testi\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"2 sisältyy 1 -testi\",\"kuvaus\":\"2 sisältyy 1 -testi\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]},{\"versio\":1,\"paivitysPvm\":1332367200000,\"voimassaAlkuPvm\":\"2012-11-20\",\"voimassaLoppuPvm\":null,\"tila\":\"HYVAKSYTTY\",\"version\":0,\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"1 sisaltaa 2 -testi\",\"kuvaus\":\"1 sisaltaa 2 -testi\",\"kayttoohje\":null,\"kasite\":null,\"kohdealue\":null,\"sitovuustaso\":null,\"kohdealueenOsaAlue\":null,\"toimintaymparisto\":null,\"tarkentaaKoodistoa\":null,\"huomioitavaKoodisto\":null,\"koodistonLahde\":null}]}]"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void updatingCodes() throws Exception {
        String koodistoUri = "updatekoodisto";

        String codes1 = mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"metadata\":[{\"nimi\": \"Update testi\"}]}"))
                .andReturn().getResponse().getContentAsString();
        codes1 = codes1.replaceFirst("\"nimi\":\"Update testi\"", "\"nimi\": \"Päivitetty Testinimi\"");
        System.out.println(codes1);
        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codes1))
                .andExpect(status().isCreated())
                .andExpect(content().string("2"));

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"metadata\":[{\"nimi\": \"Update testi\"}]}"));
        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 2))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"metadata\":[{\"nimi\": \"Päivitetty Testinimi\"}]}"));

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void invalidUpdatingCodesFails() throws Exception {
        String koodistoUri = "updatekoodisto";

        String codes1 = mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"metadata\":[{\"nimi\": \"Update testi\"}]}"))
                .andReturn().getResponse().getContentAsString();
        codes1 = codes1.replaceFirst("\"metadata\":\\[.*?\\]", "\"metadata\":[]");
        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codes1)).andExpect(status().isBadRequest())
                .andExpect(content().string("error.validation.metadata"));

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"metadata\":[{\"nimi\": \"Update testi\"}]}"));
    }


    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void deleteCodes() throws Exception {
        {
            String codesUri = "deletethisuri";
            int codesVersion = 1;
            mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", codesUri, codesVersion))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"koodistoUri\":\"deletethisuri\"}"));

            mockMvc.perform(post(BASE_PATH + "/delete/{codesUri}/{codesVersion}", codesUri, codesVersion)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isAccepted());

            mockMvc.perform(post(BASE_PATH + "/delete/{codesUri}/{codesVersion}", codesUri, codesVersion)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

        }
        {
            String codesUri = "invaliddeleteuri";
            int codesVersion = 99;
            mockMvc.perform(post(BASE_PATH + "/delete/{codesUri}/{codesVersion}", codesUri, codesVersion)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodesWithNewName() throws Exception {
        String koodistoUri = "eisuhteitaviela1";
        String codes1 = mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"metadata\":[{\"nimi\": \"1 sisaltaa 2 -testi\"}]}"))
                .andReturn().getResponse().getContentAsString();

        codes1 = codes1.replaceFirst("\"nimi\":\"1 sisaltaa 2 -testi\"", "\"nimi\": \"uusinimi\"");
        mockMvc.perform(put(BASE_PATH + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codes1)).andExpect(status().isOk())
                .andExpect(content().string("2"));

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 2))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"LUONNOS\",\"metadata\":[{\"nimi\": \"uusinimi\"}]}"));

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodesWithNewNameAndRelations() throws Exception {
        String koodistoUri = "eisuhteitaviela1";

        String codes1 = mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"tila\":\"HYVAKSYTTY\",\"metadata\":[{\"nimi\": \"1 sisaltaa 2 -testi\"}]}"))
                .andReturn().getResponse().getContentAsString();

        codes1 = codes1.replaceFirst("\"nimi\":\"1 sisaltaa 2 -testi\"", "\"nimi\": \"uusinimi\"");
        codes1 = codes1.replaceFirst("\"withinCodes\":\\[]", "\"withinCodes\":[{\"codesUri\":\"eisuhteitaviela2\",\"codesVersion\":1}]");
        codes1 = codes1.replaceFirst("\"includesCodes\":\\[]", "\"includesCodes\":[{\"codesUri\":\"eisuhteitaviela3\",\"codesVersion\":1}]");
        codes1 = codes1.replaceFirst("\"levelsWithCodes\":\\[]", "\"levelsWithCodes\":[{\"codesUri\":\"eisuhteitaviela4\",\"codesVersion\":1}]");
        mockMvc.perform(put(BASE_PATH + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(codes1)).andExpect(status().isOk())
                .andExpect(content().string("2"));

        mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", koodistoUri, 2))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"tila\":\"LUONNOS\"," +
                        "\"metadata\":[{\"nimi\": \"uusinimi\"}]," +
                        "\"withinCodes\":[{\"codesUri\":\"eisuhteitaviela2\",\"codesVersion\":1}]," +
                        "\"includesCodes\":[{\"codesUri\":\"eisuhteitaviela3\",\"codesVersion\":1}]," +
                        "\"levelsWithCodes\":[{\"codesUri\":\"eisuhteitaviela4\",\"codesVersion\":1}]}"));


    }

    @Test
    void returnsNoChangesToCodes() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "moniaversioita", 3))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"muutosTila\":\"EI_MUUTOKSIA\"}"));
    }

    @Test
    void returnsChangesToCodes() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "moniaversioita", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"muutosTila\":\"MUUTOKSIA\"}"));
    }

    @Test
    void returnsNoChangesToCodesUsingDate() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "moniaversioita", 20, 9, 2014, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"muutosTila\":\"EI_MUUTOKSIA\"}"));
    }

    @Test
    void returnsChangesToCodesUsingDate() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "moniaversioita", 20, 9, 2012, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"muutosTila\":\"MUUTOKSIA\"}"));
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "moniaversioita", 20, 9, 2013, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"muutosTila\":\"MUUTOKSIA\"}"));
    }

    @Test
    void returnsChangesToCodesWithLotsOfChanges() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonContent(3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonContent(3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(3, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));
    }


    @Test
    void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersio() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 1).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 1, 0, 1, 0, 1, 1, 1, 0, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 2).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/{codesUri}/{codesVersion}", "paljonmuutoksia", 3).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));
    }

    @Test
    void returnsChangesToCodesWithLotsOfChangesUsingDate() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 9, 2012, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonContent(3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 5, 2014, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonContent(3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 9, 2014, 0, 0, 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(3, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));
    }

    @Test
    void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersioAndDate() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 9, 2012, 0, 0, 0).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 1, 0, 1, 0, 1, 1, 1, 0, MuutosTila.MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 5, 2014, 0, 0, 0).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));
        mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codesUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", "paljonmuutoksia", 20, 9, 2014, 0, 0, 0).param("compareToLatestAccepted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tila").value(IsNull.nullValue()))
                .andExpect(content().json(getJsonContent(2, 0, 0, 0, 0, 0, 0, 0, 0, MuutosTila.EI_MUUTOKSIA)));

//        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, true).getBody(), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
//        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
//        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    private String getJsonContent(int expectedVersio, int removedMetas, int changedMetas, int addedRelations, int passiveRelations, int removedRelations, Tila expectedTila, int addedCodeElements, int changedCodeElements, int removedCodeElements, MuutosTila muutosTila) {
        return "{" +
                "\"viimeisinVersio\":" + expectedVersio + "," +
                "\"muutosTila\":\"" + muutosTila.name() + "\"," +
                "\"muuttuneetTiedot\":[" + String.join(", ", Collections.nCopies(changedMetas, "{}")) + "]," +
                "\"poistuneetTiedot\":[" + String.join(", ", Collections.nCopies(removedMetas, "{}")) + "]," +
                "\"lisatytKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(addedRelations, "{}")) + "]," +
                "\"passivoidutKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(passiveRelations, "{}")) + "]," +
                "\"poistetutKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(removedRelations, "{}")) + "]," +
                "\"tila\":\"" + expectedTila + "\"," +
                "\"lisatytKoodit\":[" + String.join(", ", Collections.nCopies(addedCodeElements, "{}")) + "]," +
                "\"muuttuneetKoodit\":[" + String.join(", ", Collections.nCopies(changedCodeElements, "{}")) + "]," +
                "\"poistetutKoodit\":[" + String.join(", ", Collections.nCopies(removedCodeElements, "{}")) + "]" +
                "}";
    }

    private String getJsonContent(int expectedVersio, int removedMetas, int changedMetas, int addedRelations, int passiveRelations, int removedRelations, int addedCodeElements, int changedCodeElements, int removedCodeElements, MuutosTila muutosTila) {
        return "{" +
                "\"viimeisinVersio\":" + expectedVersio + "," +
                "\"muutosTila\":\"" + muutosTila.name() + "\"," +
                "\"muuttuneetTiedot\":[" + String.join(", ", Collections.nCopies(changedMetas, "{}")) + "]," +
                "\"poistuneetTiedot\":[" + String.join(", ", Collections.nCopies(removedMetas, "{}")) + "]," +
                "\"lisatytKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(addedRelations, "{}")) + "]," +
                "\"passivoidutKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(passiveRelations, "{}")) + "]," +
                "\"poistetutKoodistonSuhteet\":[" + String.join(", ", Collections.nCopies(removedRelations, "{}")) + "]," +
                "\"lisatytKoodit\":[" + String.join(", ", Collections.nCopies(addedCodeElements, "{}")) + "]," +
                "\"muuttuneetKoodit\":[" + String.join(", ", Collections.nCopies(changedCodeElements, "{}")) + "]," +
                "\"poistetutKoodit\":[" + String.join(", ", Collections.nCopies(removedCodeElements, "{}")) + "]" +
                "}";
    }

  private String createKoodistoString(String koodistoUri, String codesGroupUri) throws Exception {
        //{"koodistoUri":"dummy","resourceUri":"http://localhost/8080/koodisto-service/rest/codes/dummy","omistaja":null,"organisaatioOid":"1.2.2004.6","lukittu":null,"codesGroupUri":"dummy","version":0,"versio":1,"paivitysPvm":"2012-03-21","paivittajaOid":null,"voimassaAlkuPvm":"2012-11-20","voimassaLoppuPvm":null,"tila":"HYVAKSYTTY","metadata":[{"kieli":"FI","nimi":"Dummy","kuvaus":"kuvaus","kayttoohje":null,"kasite":null,"kohdealue":null,"sitovuustaso":null,"kohdealueenOsaAlue":null,"toimintaymparisto":null,"tarkentaaKoodistoa":null,"huomioitavaKoodisto":null,"koodistonLahde":null}],"codesVersions":[],"withinCodes":[],"includesCodes":[],"levelsWithCodes":[]}
        return mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}", "dummy", 1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
                .replaceAll("\"koodistoUri\":\"dummy\"", "\"koodistoUri\":\"" + koodistoUri + "\"")
                .replaceAll("\"codesGroupUri\":\"dummy\"", "\"codesGroupUri\":\"" + codesGroupUri + "\"")
                .replaceAll("\"nimi\":\"Dummy\"", "\"nimi\":\"" + koodistoUri + "\"")
                .replaceAll("rest/codes/dummy", "rest/codes/" + koodistoUri);
    }

    private KoodistoDto newKoodistoDto(String koodistoRyhmaUri, String organisaatioOid,
                                       String koodistoUri, String nimiFi,
                                       LocalDate alkuPvm, LocalDate loppuPvm) {
        KoodistoDto dto = new KoodistoDto();
        dto.setCodesGroupUri(koodistoRyhmaUri);
        dto.setOrganisaatioOid(organisaatioOid);
        dto.setKoodistoUri(koodistoUri);
        KoodistoMetadata metadata = new KoodistoMetadata();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi(nimiFi);
        dto.setMetadata(singletonList(metadata));
        dto.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvm));
        dto.setVoimassaLoppuPvm(java.sql.Date.valueOf(loppuPvm));
        return dto;
    }

    private KoodiDto newKoodiDto(KoodistoDto koodisto, String arvo, String nimiFi) {
        return newKoodiDto(koodisto, arvo, nimiFi, LocalDate.now());
    }

    private KoodiDto newKoodiDto(KoodistoDto koodisto, String arvo, String nimiFi, LocalDate alkuPvm) {
        KoodiDto dto = new KoodiDto();
        dto.setKoodiUri(String.format("%s_%s", koodisto.getKoodistoUri(), arvo));
        dto.setKoodiArvo(arvo);
        KoodiMetadataDto metadata = new KoodiMetadataDto();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi(nimiFi);
        dto.setMetadata(singletonList(metadata));
        dto.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvm));
        return dto;
    }

}
