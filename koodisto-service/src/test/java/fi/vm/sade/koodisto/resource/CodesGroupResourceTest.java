package fi.vm.sade.koodisto.resource;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD;
import static fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/truncate_tables.sql")
@Sql("/test-data-codes-rest.sql")
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class CodesGroupResourceTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void testGetCodesByCodesUri() throws Exception {
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", -1L)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":-1,\"koodistoRyhmaUri\":\"relaatioidenlisaaminen\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"Relaatioiden lisääminen\",\"kieli\":\"FI\"}],\"koodistos\":[{},{},{},{},{}]}", true));
    }

    @Test
    void testGetCodesByCodesUriInvalid() throws Exception {
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", 0L)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodistoryhma.not.found"));
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", "")))
                .andExpect(status().is(405))
                .andExpect(content().string("error.method.not.supported"));
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", 99999L)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodistoryhma.not.found"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {ROLE_APP_KOODISTO_CRUD})
    void testUpdate() throws Exception {
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", -4L)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":-4,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"Tyhjän koodistoryhmän päivittäminen\",\"kieli\":\"FI\"}],\"koodistos\":[]}", true));
        this.mockMvc.perform(
                        put("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"id\":-4,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"paivitettunimi\",\"kieli\":\"FI\"}],\"koodistos\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":-4,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"paivitettunimi\",\"kieli\":\"FI\"}],\"koodistos\":[]}", true));
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", -4L)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":-4,\"koodistoRyhmaUri\":\"koodistoryhmanpaivittaminen\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"paivitettunimi\",\"kieli\":\"FI\"}],\"koodistos\":[]}", true));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_READ_UPDATE)
    void testUpdateInvalid() throws Exception {
        this.mockMvc.perform(
                        put("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().is(400))
                .andExpect(content().string("[\"error.metadata.empty\"]"));
        this.mockMvc.perform(
                        put("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"koodistoRyhmaMetadatas\":[{\"nimi\":\"nimi\",\"kieli\":\"FI\"}]}"))
                .andExpect(status().is(400))
                .andExpect(content().string("error.codesgroup.uri.empty"));
        this.mockMvc.perform(
                        put("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"koodistoRyhmaUri\":\"totallyvaliduri\"}"))
                .andExpect(status().is(400))
                .andExpect(content().string("[\"error.metadata.empty\"]"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_CRUD)
    void testInsert() throws Exception {
        Integer id = JsonPath.read(this.mockMvc.perform(
                        post("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"koodistoRyhmaUri\":\"newnameforcodesgroup\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"newnameforcodesgroup\",\"kieli\":\"FI\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"koodistoRyhmaUri\":\"newnameforcodesgroup\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"newnameforcodesgroup\",\"kieli\":\"FI\"}],\"koodistos\":[]}"))
                .andReturn().getResponse().getContentAsString(), "$.id");
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", id)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":" + id + ",\"koodistoRyhmaUri\":\"newnameforcodesgroup\",\"koodistoRyhmaMetadatas\":[{\"nimi\":\"newnameforcodesgroup\",\"kieli\":\"FI\"}],\"koodistos\":[]}", true));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_CRUD)
    void testInsertInvalid() throws Exception {
        this.mockMvc.perform(
                        post("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string("error.http.message.not.readable"));
        this.mockMvc.perform(
                        post("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().is(400))
                .andExpect(content().string("[\"error.metadata.empty\"]"));
        this.mockMvc.perform(
                        post("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"koodistoRyhmaUri\":\"totallyvaliduri\"}"))
                .andExpect(status().is(400))
                .andExpect(content().string("[\"error.metadata.empty\"]"));
        this.mockMvc.perform(
                        post("/rest/codesgroup/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"koodistoRyhmaMetadatas\":[{\"nimi\":\"\",\"kieli\":\"FI\"}]}"))
                .andExpect(status().is(400))
                .andExpect(content().string("error.metadata.empty"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_CRUD)
    void testDelete() throws Exception {
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", -3L)))
                .andExpect(status().isOk());
        this.mockMvc.perform(post(String.format("/rest/codesgroup/delete/%s", -3L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
        this.mockMvc.perform(get(String.format("/rest/codesgroup/%s", -3L)))
                .andExpect(status().isNotFound());
    }

    //
    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_CRUD)
    void testDeleteInvalid() throws Exception {
        this.mockMvc.perform(post(String.format("/rest/codesgroup/delete/%s", 0L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodistoryhma.not.found"));
        this.mockMvc.perform(post(String.format("/rest/codesgroup/delete/%s", 99999L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodistoryhma.not.found"));
    }
}
