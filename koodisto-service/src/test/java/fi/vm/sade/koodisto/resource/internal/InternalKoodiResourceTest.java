package fi.vm.sade.koodisto.resource.internal;

import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.util.KoodistoRole;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-internal-rest.sql"})
class InternalKoodiResourceTest {
    private final String BASE_PATH = "/internal/koodi";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Description("Test get koodiPage")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetInternalKoodiPage() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/get_1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("/fixtures/resource/internal/koodi/koodiPage.json")));
    }

    @Test
    @Description("Test get koodiPage with invalid version")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetInternalKoodiPageBadRequest() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/get_1/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Description("Test get endpoint")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetInternalKoodi() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/get"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"get_1\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"2012-03-22\"")));
    }

    @Test
    @Description("Delete with invalid access rights")
    void testDeleteInternalKoodiNoAccess() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/get_1/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Description("Delete with invalid version")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testDeleteInternalKoodiInvalidVersion() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/nonexistent/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Description("Delete with non-existent koodi")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testDeleteInternalKoodiNotFound() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/nonexistent/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Description("Delete HYVAKSYTTY should fail (e.g. locked)")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testDeleteInternalKoodiIsLocked() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/get_1/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.codes.version.locked"));
    }

    @Test
    @Description("Delete PASSIVINEN should be ok (e.g. anomaly), backwards compatibility")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testDeleteInternalKoodiIsPassiivinen() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/removable/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @Description("Delete insufficient access rights")
    @WithMockUser(value = "1.2.3.4.5")
    void testDeleteInternalKoodiInsufficientAccessRights() throws Exception {
        this.mockMvc.perform(delete("/internal/koodi/removable/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Description("Post to not found koodisto")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal01() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodisto.not.found"));
    }

    @Test
    @Description("Post empty rows is bad request")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal02() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("upsertKoodiByKoodisto.koodis: error.koodi.list.empty"));
    }

    @Test
    @Description("Post null body is bad request")
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal03() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/dummy")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/internal/koodi/upsert/dummy")
                        .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Description("Posting empty name is bad request")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal04() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("upsertKoodiByKoodisto.koodis[0].metadata[0].nimi: error.nimi.empty"));
    }

    @Test
    @Description("Posting can add one koodi")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal1() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodistoUri\":\"one\"")));
        this.mockMvc.perform(get("/internal/koodi/one"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"one_1\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now(ZoneId.of("UTC")) + "\"")));
    }

    @Test
    @Description("Posting can edit one koodi")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal2() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/two"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"two_1\"")))
                .andExpect(content().string(containsString("\"metadata\":[{\"nimi\":\"two1\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"2012-03-22\"")));
        this.mockMvc.perform(post("/internal/koodi/upsert/two")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodistoUri\":\"two\"")));
        this.mockMvc.perform(get("/internal/koodi/two"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"two_1\"")))
                .andExpect(content().string(containsString("\"metadata\":[{\"nimi\":\"UPDATED\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now(ZoneId.of("UTC")) + "\"")));
    }

    @Test
    @Description("Posting can add one and edit one koodi")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal3() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/two"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"two_1\"")))
                .andExpect(content().string(containsString("\"metadata\":[{\"nimi\":\"two1\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"2012-03-22\"")));
        this.mockMvc.perform(post("/internal/koodi/upsert/two")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"UPDATED\"," +
                                "\"kuvaus\":\"UPDATED\"," +
                                "\"lyhytNimi\":\"UPDATED\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}," +
                                "{" +
                                "\"koodiArvo\":\"2\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"ADDED\"," +
                                "\"kuvaus\":\"ADDED\"," +
                                "\"lyhytNimi\":\"ADDED\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"ADDED\"," +
                                "\"kuvaus\":\"ADDED\"," +
                                "\"lyhytNimi\":\"ADDED\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"ADDED\"," +
                                "\"kuvaus\":\"ADDED\"," +
                                "\"lyhytNimi\":\"ADDED\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodistoUri\":\"two\"")));
        this.mockMvc.perform(get("/internal/koodi/two"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"two_1\"")))
                .andExpect(content().string(containsString("\"metadata\":[{\"nimi\":\"UPDATED\"")))
                .andExpect(content().string(containsString("\"metadata\":[{\"nimi\":\"ADDED\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now(ZoneId.of("UTC")) + "\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"2\",\"paivitysPvm\":\"" + LocalDate.now(ZoneId.of("UTC")) + "\"")));
    }

    @Test
    @Description("Posting not found koodisto")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal4() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/upsert/notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{" +
                                "\"koodiArvo\":\"1\"," +
                                "\"metadata\":[" +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"FI\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"EN\"}," +
                                "{\"nimi\":\"ONE\"," +
                                "\"kuvaus\":\"ONE\"," +
                                "\"lyhytNimi\":\"ONE\"," +
                                "\"kieli\":\"SV\"}]" +
                                "}]"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE})
    @Description("Test getting koodi-list for koodisto")
    void getKoodistoKoodis() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/koodisto/two/1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(
                                "[{\"koodisto\":{\"koodistoUri\":\"two\"},\"koodiArvo\":\"1\",\"versio\":1,\"koodiUri\":\"two_1\",\"paivitysPvm\":\"2012-03-22\",\"paivittajaOid\":null,\"voimassaAlkuPvm\":\"1990-01-01\",\"metadata\":[{\"nimi\":\"two1\",\"kuvaus\":\"Two 1\",\"kieli\":\"FI\"}],\"sisaltyyKoodeihin\":[],\"sisaltaaKoodit\":[],\"rinnastuuKoodeihin\":[]}]")
                );

    }


    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void updateKoodi() throws Exception {

        String koodiUri = "two_1";
        String koodiVersio = "1";
        JSONArray metadata = new JSONArray();
        JSONObject metadata1 = new JSONObject();
        metadata1.put("kieli", "FI");
        metadata1.put("nimi", "muokattu");
        metadata.put(metadata1);
        JSONObject o = new JSONObject();
        o.put("koodiUri", koodiUri);
        o.put("versio", koodiVersio);
        o.put("koodiArvo", "two1");
        o.put("voimassaAlkuPvm", "2022-01-01");
        o.put("version", "1");
        o.put("tila", "LUONNOS");
        o.put("metadata", metadata);

        mockMvc.perform(get(BASE_PATH + "/{koodiUri}/{koodiVersio}", koodiUri, koodiVersio))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.koodiUri").value("two_1"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("two1")
                );

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.koodiUri").value("two_1"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("muokattu")
                );

        o.put("tila", "PASSIIVINEN");
        metadata1.put("nimi", "muokattu2");
        o.put("lockingVersion", "2");

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.tila").value("PASSIIVINEN"),
                        jsonPath("$.koodiUri").value("two_1"),
                        jsonPath("$.metadata[0].nimi").value("muokattu2")
                );

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void createKoodi() throws Exception {
        String koodiArvo = "new";
        JSONArray metadata = new JSONArray();
        JSONObject metadata1 = new JSONObject();
        metadata1.put("kieli", "FI");
        metadata1.put("nimi", "newnimi");
        metadata.put(metadata1);
        JSONObject o = new JSONObject();
        o.put("koodiArvo", koodiArvo);
        o.put("voimassaAlkuPvm", "2022-01-01");
        o.put("metadata", metadata);
        mockMvc.perform(post(BASE_PATH + "/dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(o.toString()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.versio").value("1"),
                        jsonPath("$.koodiUri").value("dummy_new"),
                        jsonPath("$.tila").value("LUONNOS"),
                        jsonPath("$.metadata[0].nimi").value("newnimi")
                );
    }

    private String readFile(String fileName) throws Exception {
        return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource(fileName)).toURI()), StandardCharsets.UTF_8);
    }
}
