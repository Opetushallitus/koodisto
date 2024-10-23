package fi.vm.sade.koodisto.resource.internal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-internal-suhde.sql"})
@WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
class InternalResourceSuhdeTest {
    @Autowired
    private MockMvc mockMvc;
    String baseObjectString = readFile("/fixtures/resource/internal/koodi/relationBaseKoodi.json");

    InternalResourceSuhdeTest() throws Exception {
    }

    @Test
    @DisplayName("Test getting base object")
    void testBaseObject() throws Exception {
        JSONObject o = new JSONObject(baseObjectString);
        this.mockMvc.perform(get("/internal/koodi/one_1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(o.toString()));
    }

    @Test
    @DisplayName("Test saving object with no modifications")
    void testModifyRelations1() throws Exception {
        JSONObject input = new JSONObject(baseObjectString);
        JSONObject output = new JSONObject(baseObjectString);
        output.put("lockingVersion", 2);
        output.put("paivittajaOid", "1.2.3.4.5");
        output.put("paivitysPvm", LocalDate.now(ZoneId.of("UTC")));
        this.mockMvc.perform(put("/internal/koodi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(output.toString()));
    }

    @Test
    @DisplayName("Test adding and removing relations")
    void testModifyRelations2() throws Exception {
        JSONObject input = new JSONObject(baseObjectString);
        input.put("sisaltyyKoodeihin", new JSONArray("[{\"koodiUri\": \"two_1\",\"koodiVersio\":1}]"));
        input.put("sisaltaaKoodit", new JSONArray("[{\"koodiUri\": \"three_1\",\"koodiVersio\":1}]"));
        input.put("rinnastuuKoodeihin", new JSONArray("[{\"koodiUri\": \"four_1\",\"koodiVersio\":1}]"));

        this.mockMvc.perform(put("/internal/koodi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input.toString()))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/internal/koodi/one_1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"sisaltyyKoodeihin\":[{\"koodiUri\": \"two_1\"}]," +
                        "\"sisaltaaKoodit\":[{\"koodiUri\": \"three_1\"}]," +
                        "\"rinnastuuKoodeihin\":[{\"koodiUri\": \"four_1\"}]}", false));

        input.put("rinnastuuKoodeihin", new JSONArray());
        input.put("lockingVersion", 2);

        this.mockMvc.perform(put("/internal/koodi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input.toString()))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/internal/koodi/one_1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"sisaltyyKoodeihin\":[{\"koodiUri\": \"two_1\"}]," +
                        "\"sisaltaaKoodit\":[{\"koodiUri\": \"three_1\"}]," +
                        "\"rinnastuuKoodeihin\":[]}", false));

        input.put("sisaltyyKoodeihin", new JSONArray());
        input.put("sisaltaaKoodit", new JSONArray());
        input.put("lockingVersion", 3);

        this.mockMvc.perform(put("/internal/koodi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input.toString()))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/internal/koodi/one_1/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"sisaltyyKoodeihin\":[]," +
                        "\"sisaltaaKoodit\":[]," +
                        "\"rinnastuuKoodeihin\":[]}", false));

    }

    private static String readFile(String fileName) throws Exception {
        return Files.readString(Paths.get(Objects.requireNonNull(InternalResourceSuhdeTest.class.getResource(fileName)).toURI()), StandardCharsets.UTF_8);
    }
}
