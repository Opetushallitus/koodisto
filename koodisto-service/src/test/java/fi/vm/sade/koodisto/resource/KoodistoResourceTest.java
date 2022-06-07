package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql("/truncate_tables.sql")
@Sql("/test-data.sql")
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class KoodistoResourceTest {
    private static final String BASE_PATH = "/rest/json";
    @Autowired
    private KoodistoResource koodistoResource;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testListAllKoodistoRyhmas() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void testGetKoodistoAsPropertiesDefaultLang() throws Exception {
        String koodistoUri = "koodisto17";
        mockMvc.perform(get(BASE_PATH + "/{koodistoUri}.properties", koodistoUri))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "471=koodi30\n" +
                                "472=koodi31")));
    }

    @Test
    void testGetKoodistoAsProperties() throws Exception {
        String koodistoUri = "koodisto17";
        String lang = "EN";
        mockMvc.perform(get(BASE_PATH + "/{koodistoUri}_{lang}.properties", koodistoUri, lang))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "471=\n" +
                                "472=")));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testUpdateKoodiLangMetaData() throws Exception {
        String koodistoUri = "koodisto17";
        String koodiUri = "471";
        String lang = "EN";
        mockMvc.perform(post(BASE_PATH + "/{koodistoUri}/koodi/{koodiUri}/kieli/{lang}/metadata", koodistoUri, koodiUri, lang)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nimi", "NAME")
                        .param("kuvaus", "DESCRIPT"))
                .andExpectAll(
                        status().isOk(),
                        content().json("{\"metadata\":[{\"nimi\": \"koodi30\",\"kieli\": \"FI\"},{\"nimi\": \"NAME\",\"kieli\": \"EN\"}] }")
                );
        lang = "FI";
        mockMvc.perform(post(BASE_PATH + "/{koodistoUri}/koodi/{koodiUri}/kieli/{lang}/metadata", koodistoUri, koodiUri, lang)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nimi", "NIMI")
                        .param("kuvaus", "KUVAUS"))
                .andExpectAll(
                        status().isOk(),
                        content().json("{\"metadata\":[{\"nimi\": \"NIMI\",\"kieli\": \"FI\"},{\"nimi\": \"NAME\",\"kieli\": \"EN\"}] }")
                );
    }

    @Test
    void testGetKoodistoByUri() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 2;
        KoodistoDto koodisto = koodistoResource.getKoodistoByUri(koodistoUri, null);
        Assertions.assertEquals(koodistoUri, koodisto.getKoodistoUri());
        Assertions.assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test
    void testGetKoodistoByUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 1;
        KoodistoDto koodisto = koodistoResource.getKoodistoByUri(koodistoUri, koodistoVersio);
        Assertions.assertEquals(koodistoUri, koodisto.getKoodistoUri());
        Assertions.assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test
    void testGetNonExistingKoodistoByUri() {
        final String koodistoUri = "ei-ole-olemassa";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodistoByUri(koodistoUri, null));
    }

    @Test
    void testGetNonExistingKoodistoByUriAndVersio() {
        final String koodistoUri = "ei-ole-olemassa";
        final int koodistoVersio = 1;
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodistoByUri(koodistoUri, koodistoVersio));
    }

    @Test
    void testGetKoodisByKoodistoUri() {
        final String koodistoUri = "koodisto17";

        List<KoodiDto> koodis = koodistoResource.getKoodisByKoodisto(koodistoUri, null, false);
        Assertions.assertEquals(2, koodis.size());
    }

    @Test
    void testGetKoodisByKoodistoUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 1;

        List<KoodiDto> koodis =
                koodistoResource.getKoodisByKoodisto(koodistoUri, koodistoVersio, false);
        Assertions.assertEquals(1, koodis.size());
    }

    @Test
    void testGetKoodisByNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodisByKoodisto(koodistoUri, null, false));
    }

    @Test
        //KoodistoNotFoundException.class
    void testGetKoodisByNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "ei-ole-olemassa";
        final int koodistoVersio = 1;
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodisByKoodisto(koodistoUri, koodistoVersio, false));
    }

    @Test
    void testGetKoodisByArvoWithKoodistoUri() {
        final String koodistoUri = "koodisto17";
        final String koodiArvo = "28";

        final String koodiUri = "471";
        final int koodiVersio = 2;

        List<KoodiDto> koodis =
                koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, null);
        Assertions.assertEquals(1, koodis.size());

        KoodiDto koodi = koodis.get(0);
        Assertions.assertEquals(koodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    void testGetKoodisByArvoWithKoodistoUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 1;
        final String koodiArvo = "28";

        final String koodiUri = "471";
        final int koodiVersio = 1;

        List<KoodiDto> koodis =
                koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
        Assertions.assertEquals(1, koodis.size());

        KoodiDto koodi = koodis.get(0);
        Assertions.assertEquals(koodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
        //KoodistoNotFoundException.class
    void testGetKoodisByArvoWithNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        final String koodiArvo = "123";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, null));
    }

    @Test
    void testGetKoodisByArvoWithNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 3;
        final String koodiArvo = "123";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio));
    }

    @Test
    void testGetKoodiByUriWithKoodistoUri() {
        final String koodistoUri = "koodisto17";
        final String koodiUri = "471";
        final int koodiVersio = 2;

        KoodiDto koodi = koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null);
        Assertions.assertEquals(koodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    void testGetKoodiByUriWithKoodistoUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 1;
        final String koodiUri = "471";
        final int koodiVersio = 1;

        KoodiDto koodi = koodistoResource.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
        Assertions.assertEquals(koodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    void testGetKoodiByUriWithNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        final String koodiUri = "471";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null));
    }

    @Test
    void testGetKoodiByUriWithNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "koodisto17";
        final int koodistoVersio = 3;
        final String koodiUri = "471";
        assertThrows(KoodistoNotFoundException.class, () -> koodistoResource.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio));
    }

    @Test
    void testGetKoodiByNonExistingUri() {
        final String koodistoUri = "koodisto17";
        final String koodiUri = "ei-ole-olemassa";
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null));
    }

    @Test
    void testGetAlakoodisByKoodiUri() {
        final String koodiUri = "473";

        List<KoodiDto> koodis = koodistoResource.getAlakoodis(koodiUri, null);
        Assertions.assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetAlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "473";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoResource.getAlakoodis(koodiUri, koodiVersio);
        Assertions.assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetYlakoodisByKoodiUri() {
        final String koodiUri = "474";

        List<KoodiDto> koodis = koodistoResource.getYlakoodis(koodiUri, null);
        Assertions.assertEquals(2, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetYlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "474";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoResource.getYlakoodis(koodiUri, koodiVersio);
        Assertions.assertEquals(2, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetRinnasteinenByKoodiUri() {
        final String koodiUri = "475";

        List<KoodiDto> koodis = koodistoResource.getRinnasteinenKoodis(koodiUri, null);
        Assertions.assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetRinnasteinenByKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoResource.getRinnasteinenKoodis(koodiUri, koodiVersio);
        Assertions.assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        Assertions.assertEquals(alakoodiUri, koodi.getKoodiUri());
        Assertions.assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    void testGetAlakoodiByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getAlakoodis(koodiUri, null));
    }

    @Test
    void testGetAlakoodiByNonExistingKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 2;
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getAlakoodis(koodiUri, koodiVersio));
    }

    @Test
    void testGetYlakoodiByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getYlakoodis(koodiUri, null));
    }

    @Test
    void testGetYlakoodiByNonExistingKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 2;
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getYlakoodis(koodiUri, koodiVersio));
    }

    @Test
    void testGetRinnasteinenByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getRinnasteinenKoodis(koodiUri, null));
    }

    @Test
    void testGetRinnasteinenByNonExistingKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 2;
        assertThrows(KoodiNotFoundException.class, () -> koodistoResource.getRinnasteinenKoodis(koodiUri, koodiVersio));
    }

    @Test
    void testSearchKoodis() throws Exception {
        // all
        Assertions.assertEquals(0, koodistoResource.searchKoodis(null, null, null, null, null, null).size()); // Empty search disabled for performance reasons
        // by koodiuri
        Assertions.assertEquals(1, koodistoResource.searchKoodis(Collections.singletonList("475"), null, null, null, null, null).size());
        // by koodiarvo
        Assertions.assertEquals(6, koodistoResource.searchKoodis(null, "3", null, null, null, null).size());
        // by tila
        Assertions.assertEquals(0, koodistoResource.searchKoodis(null, null, Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());  // Empty search disabled for performance reasons
        Assertions.assertEquals(1, koodistoResource.searchKoodis(null, "27", Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());
        // by validAtDate
        Assertions.assertEquals(134, koodistoResource.searchKoodis(null, null, null, "2013-01-01", null, null).size());
        // by versio & versioselectiontype
        Assertions.assertEquals(0, koodistoResource.searchKoodis(null, null, null, null, 2, SearchKoodisVersioSelectionType.SPECIFIC).size()); // Empty search disabled for performance reasons
        Assertions.assertEquals(2, koodistoResource.searchKoodis(null, "versio 10", null, null, 10, SearchKoodisVersioSelectionType.SPECIFIC).size());
    }

    @Test
    void testGetKoodisByKoodistoOnlyValidKoodis() {
        // all
        Assertions.assertEquals(2, koodistoResource.getKoodisByKoodisto("paljon_versioita.fi/1", null, false).size());
        // only valid
        Assertions.assertEquals(1, koodistoResource.getKoodisByKoodisto("paljon_versioita.fi/1", null, true).size());
    }

}
