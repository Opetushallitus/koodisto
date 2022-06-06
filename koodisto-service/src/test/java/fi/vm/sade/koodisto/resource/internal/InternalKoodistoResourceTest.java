package fi.vm.sade.koodisto.resource.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/truncate_tables.sql"})
@Sql({"/test-data-internal-rest.sql"})
class InternalKoodistoResourceTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Description("Get koodisto list")
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD})
    void testGetKoodistoList() throws Exception {
        this.mockMvc
                .perform(get("/internal/koodisto"))
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
                .perform(get("/internal/koodisto/get/1"))
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

}