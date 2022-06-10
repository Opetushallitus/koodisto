package fi.vm.sade.koodisto.resource.internal;

import fi.vm.sade.koodisto.util.KoodistoRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-ryhma.sql"})
class InternalKoodistoRyhmaResourceTest {
    private final String BASE_PATH = "/internal/koodistoryhma";
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void insertKoodistoRyhma() throws Exception {
        mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.koodistoRyhmaUri").value("uusi")
                );
        mockMvc.perform(get(BASE_PATH + "/empty/")).andExpectAll(
                status().isOk(),
                jsonPath("$.length()").value(4)
        );
        mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("error.koodistoryhma.already.exists")
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void updateKoodistoRyhma() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/{koodistoRyhmaUri}", "a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.koodistoRyhmaUri").value("a"),
                        jsonPath("$.nimi.fi").value("uusi")
                );
        mockMvc.perform(get(BASE_PATH + "/empty/")).andExpectAll(
                status().isOk(),
                jsonPath("$.length()").value(3),
                content().json("[{\"koodistoRyhmaUri\":\"a\",\"nimi\": {\"fi\":\"uusi\"}},{\"koodistoRyhmaUri\":\"b\"},{\"koodistoRyhmaUri\":\"c\"}]")
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void deleteKoodistoRyhma() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "a")).andExpectAll(
                status().isNoContent()
        );
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "dummy")).andExpectAll(
                status().isBadRequest()
        );
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "foo")).andExpectAll(
                status().isNotFound()
        );
        mockMvc.perform(get(BASE_PATH + "/empty/")).andExpectAll(
                status().isOk(),
                jsonPath("$.length()").value(2)
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE})
    void getKoodistoRyhma() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/a")).andExpectAll(
                status().isOk(),
                content().json("{\"koodistoRyhmaUri\":\"a\"}")
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE})
    void getEmptyKoodistoRyhma() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/empty/")).andExpectAll(
                status().isOk(),
                jsonPath("$.length()").value(3),
                content().json("[{\"koodistoRyhmaUri\":\"a\"},{\"koodistoRyhmaUri\":\"b\"},{\"koodistoRyhmaUri\":\"c\"}]")
        );
    }

    @Test
    void unAuthenticated() throws Exception {
        mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FOUND.value())
                );
        mockMvc.perform(put(BASE_PATH + "/{koodistoRyhmaUri}", "a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FOUND.value())
                );
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "a")).andExpectAll(
                status().is(HttpStatus.FOUND.value())
        );
        mockMvc.perform(get(BASE_PATH + "/empty/")).andExpectAll(
                status().is(HttpStatus.FOUND.value())
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5")
    void unAuthorized() throws Exception {
        mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FORBIDDEN.value())
                );
        mockMvc.perform(put(BASE_PATH + "/{koodistoRyhmaUri}", "a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FORBIDDEN.value())
                );
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "a")).andExpectAll(
                status().is(HttpStatus.FORBIDDEN.value())
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE})
    void unAuthorizedReadOnlyRole() throws Exception {
        mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FORBIDDEN.value())
                );
        mockMvc.perform(put(BASE_PATH + "/{koodistoRyhmaUri}", "a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nimi\":{\"fi\":\"uusi\",\"sv\":\"uusi\",\"en\":\"uusi\"}}"))
                .andExpectAll(
                        status().is(HttpStatus.FORBIDDEN.value())
                );
        mockMvc.perform(delete(BASE_PATH + "/{koodistoRyhmaUri}", "a")).andExpectAll(
                status().is(HttpStatus.FORBIDDEN.value())
        );
    }
}