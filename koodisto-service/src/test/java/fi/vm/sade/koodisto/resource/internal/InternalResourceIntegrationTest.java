package fi.vm.sade.koodisto.resource.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-internal-rest.sql"})
class InternalResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Description("Test get enpoint")
    @WithMockUser(authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetInternalKoodi() throws Exception {
        this.mockMvc.perform(get("/internal/koodi/get"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"koodiUri\":\"get_1\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"2012-03-22\"")));
    }

    @Test
    @Description("Post to not found koodisto")
    @WithMockUser(authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal01() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/notfound")
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
    @WithMockUser(authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal02() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.koodi.list.empty"));
    }

    @Test
    @Description("Post null body is bad request")
    @WithMockUser(authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal03() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/dummy")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/internal/koodi/dummy")
                        .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Description("Posting empty name is bad request")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal04() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/one")
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
                .andExpect(content().string("error.nimi.empty"));
    }
    @Test
    @Description("Posting can add one koodi")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal1() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/one")
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
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now() + "\"")));
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
        this.mockMvc.perform(post("/internal/koodi/two")
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
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now() + "\"")));
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
        this.mockMvc.perform(post("/internal/koodi/two")
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
                .andExpect(content().string(containsString("\"koodiArvo\":\"1\",\"paivitysPvm\":\"" + LocalDate.now() + "\"")))
                .andExpect(content().string(containsString("\"koodiArvo\":\"2\",\"paivitysPvm\":\"" + LocalDate.now() + "\"")));
    }

    @Test
    @Description("Posting not found koodisto")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testPostInternal4() throws Exception {
        this.mockMvc.perform(post("/internal/koodi/notfound")
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

}