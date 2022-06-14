package fi.vm.sade.koodisto.resource.internal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-internal-rest.sql"})
class InternalKoodistoResourceTest {
    private final String BASE_PATH = "/internal/koodisto";
    @Autowired
    private MockMvc mockMvc;


    @Test
    @Description("Get koodisto list")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetKoodistoList() throws Exception {
        this.mockMvc
                .perform(get(BASE_PATH))
                .andExpect(content().string(containsString("{" +
                        "\"koodistoRyhmaMetadata\":[{\"id\":-1,\"uri\":\"general\",\"nimi\":\"general\",\"kieli\":\"FI\"}]," +
                        "\"koodistoUri\":\"dummy\",\"versio\":1," +
                        "\"voimassaAlkuPvm\":\"2012-11-20\"," +
                        "\"voimassaLoppuPvm\":null," +
                        "\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"Dummy\",\"kuvaus\":\"kuvaus\"}]," +
                        "\"koodiCount\":0}")))
                .andExpect(status().isOk());
    }

    @Test
    @Description("Get koodisto page data")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetKoodistoPage() throws Exception {
        this.mockMvc
                .perform(get(BASE_PATH + "/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"koodistoRyhmaMetadata\":[{" +
                                "\"id\":-1," +
                                "\"uri\":\"general\"," +
                                "\"nimi\":\"general\"," +
                                "\"kieli\":\"FI\"}]," +
                                "\"resourceUri\":\"http://localhost/8080/koodisto-service/rest/codes/get\"," +
                                "\"koodistoUri\":\"get\"," +
                                "\"versio\":1," +
                                "\"organisaatioOid\":\"1.2.2004.6\"," +
                                "\"paivitysPvm\":\"2012-03-22\"," +
                                "\"paivittajaOid\":null," +
                                "\"voimassaAlkuPvm\":\"2012-11-20\"," +
                                "\"voimassaLoppuPvm\":null," +
                                "\"tila\":\"HYVAKSYTTY\"," +
                                "\"metadata\":[{\"kieli\":\"FI\",\"nimi\":\"get\",\"kuvaus\":\"get\"}]," +
                                "\"koodiVersio\":[1]," +
                                "\"sisaltyyKoodistoihin\":[]," +
                                "\"sisaltaaKoodistot\":[]," +
                                "\"rinnastuuKoodistoihin\":[]," +
                                "\"koodiList\":[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"versio\":1," +
                                "\"paivitysPvm\":\"2012-03-22\"," +
                                "\"paivittajaOid\":null," +
                                "\"voimassaAlkuPvm\":\"1990-01-01\"," +
                                "\"metadata\":[{\"nimi\":\"get1\",\"kieli\":\"FI\"}]}]}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void updateKoodistoValidation() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("[" +
                                "\"error.organisaatioOid.blank\"," +
                                "\"error.koodistoRyhmaUri.blank\"," +
                                "\"error.koodistoUri.blank\"," +
                                "\"error.versio.less.than.one\"," +
                                "\"error.lockingVersion.less.than.one\"," +
                                "\"error.tila.empty\"," +
                                "\"error.metadata.empty\"" +
                                "]")
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void updateKoodisto() throws Exception {

        String koodistoUri = "two";
        String koodistoVersio = "1";
        JSONArray metadata = new JSONArray();
        JSONObject metadata1 = new JSONObject();
        metadata1.put("kieli", "FI");
        metadata1.put("nimi", "muokattu");
        metadata.put(metadata1);
        JSONObject o = new JSONObject();
        o.put("koodistoUri", koodistoUri);
        o.put("koodistoRyhmaUri", "dummy");
        o.put("organisaatioOid", "1.2.2004.6");
        o.put("versio", koodistoVersio);
        o.put("lockingVersion", "1");
        o.put("tila", "LUONNOS");
        o.put("metadata", metadata);

        mockMvc.perform(get(BASE_PATH + "/{koodistoUri}/{koodistoVersio}", koodistoUri, koodistoVersio))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.koodistoRyhmaUri").value("general"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("Twot")
                );

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.koodistoRyhmaUri").value("dummy"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("muokattu")
                );

        o.put("tila", "HYVAKSYTTY");
        metadata1.put("nimi", "muokattu2");
        o.put("lockingVersion", "2");

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.tila").value("HYVAKSYTTY"),
                        jsonPath("$.koodistoRyhmaUri").value("dummy"),
                        jsonPath("$.metadata[0].nimi").value("muokattu2")
                );

        metadata1.put("nimi", "muokattu3");
        o.put("lockingVersion", "3");

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("2"),
                        jsonPath("$.koodistoRyhmaUri").value("dummy"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("muokattu3")
                );
    }
}