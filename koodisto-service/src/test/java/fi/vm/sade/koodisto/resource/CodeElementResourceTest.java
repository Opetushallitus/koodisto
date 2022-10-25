package fi.vm.sade.koodisto.resource;

import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.util.FieldLengths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql("/truncate_tables.sql")
@Sql("/test-data-multiple-relations.sql")
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class CodeElementResourceTest {
    private static final String BASE_PATH = "/rest/codeelement";

    @Autowired
    private KoodiBusinessService service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/addrelations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));

        mockMvc.perform(post(BASE_PATH + "/removerelations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));

        mockMvc.perform(put(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void meaningfulResponseCodeIfErrorOccurs() throws Exception {
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("koodi")));
        addRelations("codeelementuri", "SISALTYY", kr)
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodi.not.found"));
        removeRelations("codeelementuri", "SISALTYY", kr)
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodi.not.found"));
        kr.put("relations", new JSONArray(List.of("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3")));
        addRelations("codeelementuri", "asd", kr)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));
        removeRelations("codeelementuri", "asd", kr)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("error.http.message.not.readable"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void removesMultipleCodeElementRelationsWithTypeRINNASTEINEN() throws Exception {
        String codeElementUri = "sisaltaakoodisto1koodit";
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3")));
        removeRelations(codeElementUri, "RINNASTEINEN", kr)
                .andExpect(status().isOk());
        Assertions.assertTrue(service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).isEmpty());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsMultipleCodeElementRelationsWithTypeRINNASTEINEN() throws Exception {
        String codeElementUri = "lisaarinnasteinen14";
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("lisaarinnasteinen14kanssa1", "lisaarinnasteinen14kanssa2", "lisaarinnasteinen14kanssa3")));
        Assertions.assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        addRelations(codeElementUri, "RINNASTEINEN", kr).andExpect(status().isOk());
        Assertions.assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void removesMultipleCodeElementRelationsWithTypeSISALTYY() throws Exception {
        String codeElementUri = "sisaltaakoodisto1koodit";
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2")));
        removeRelations(codeElementUri, "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsMultipleCodeElementRelationsWithTypeSISALTYY() throws Exception {
        String codeElementUri = "lisaasisaltyy18";
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2")));
        kr.put("isChild", "false");
        Assertions.assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        addRelations(codeElementUri, "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void removesMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() throws Exception {
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2", "sisaltyysuhde4kanssa3")));
        kr.put("isChild", "true");
        removeRelations("sisaltyykoodisto1koodienkanssa", "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa1", 2, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa1", 1, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa2", 2, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa2", 1, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa3", 2, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa3", 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() throws Exception {
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("lisaasisaltyy18kanssa3", "lisaasisaltyy18kanssa4")));
        kr.put("isChild", "true");
        addRelations("lisaasisaltyy18", "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa3", 2, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa3", 1, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa4", 2, false, SuhteenTyyppi.SISALTYY).size());
        Assertions.assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa4", 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void removesMultipleCodeElementRelationsThatBelongToDifferentCodes() throws Exception {
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(List.of("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3")));
        kr.put("isChild", "false");
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        removeRelations(codeElementUri, "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void addsMultipleCodeElementRelationsThatBelongToDifferentCodes() throws Exception {
        JSONObject kr = new JSONObject();
        kr.put("relations", new JSONArray(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3")));
        kr.put("isChild", "false");
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        removeRelations(codeElementUri, "SISALTYY", kr).andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    void testGetAllCodeElementVersionsByCodeElementUri() throws Exception {
        getAllCodeElementVersionsByCodeElementUri("sisaltyysuhde4kanssa1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].koodiUri").value("sisaltyysuhde4kanssa1"));
        getAllCodeElementVersionsByCodeElementUri("montaversiota")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetAllCodeElementVersionsByCodeElementUriInvalid() throws Exception {
        getAllCodeElementVersionsByCodeElementUri("invaliduri")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetCodeElementByUriAndVersion() throws Exception {
        getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 1)
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(jsonPath("$.koodiArvo").value("ss4k1"))
                .andExpect(jsonPath("$.koodiUri").value("sisaltyysuhde4kanssa1"))
                .andExpect(jsonPath("$.tila").value("HYVAKSYTTY"))
                .andExpect(jsonPath("$.versio").value("1"))
                .andExpect(jsonPath("$.koodisto.koodistoUri").value("sisaltyysuhde2kanssa"));
    }

    @Test
    void testGetCodeElementByUriAndVersionInvalid() throws Exception {
        getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 0)
                .andExpect(status().isBadRequest());

        getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", -1)
                .andExpect(status().isBadRequest());

        getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 9999)
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodi.not.found"));

        getCodeElementByUriAndVersion("invaliduriisnotfound", 1)
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodi.not.found"));
    }

    @Test
    void testGetCodeElementByCodeElementUri() throws Exception {
        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "sisaltyysuhde4kanssa1")
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(jsonPath("$.koodiArvo").value("ss4k1"))
                .andExpect(jsonPath("$.koodiUri").value("sisaltyysuhde4kanssa1"))
                .andExpect(jsonPath("$.tila").value("HYVAKSYTTY"))
                .andExpect(jsonPath("$.versio").value("1"))
                .andExpect(jsonPath("$.koodisto.koodistoUri").value("sisaltyysuhde2kanssa"));
    }

    @Test
    void testGetCodeElementByCodeElementUriInvalid() throws Exception {
        getCodeElementByCodeElementUri("invalidcodeelementuri", 1, "sisaltyysuhde4kanssa1").andExpect(status().isNotFound()).andExpect(content().string("error.koodisto.not.found"));

        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 0, "sisaltyysuhde4kanssa1").andExpect(status().isBadRequest()).andExpect(content().string(containsString("error.validation.version")));
        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", -1, "sisaltyysuhde4kanssa1").andExpect(status().isBadRequest()).andExpect(content().string(containsString("error.validation.version")));
        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 9999, "sisaltyysuhde4kanssa1").andExpect(status().isNotFound()).andExpect(content().string("error.koodisto.not.found"));

        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "invalidcodesuri").andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "").andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, null).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
    }

    @Test
    void testGetAllCodeElementsByCodesUri() throws Exception {
        String res = getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)).andReturn().getResponse().getContentAsString();
        getAllCodeElementsByCodesUri("lisaarinnasteinen2")
                .andExpect(status().isOk())
                .andExpect(content().string(res));
    }

    @Test
    void testGetAllCodeElementsByCodesUriAndVersion() throws Exception {
        getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }
    @Test
    void testGetAllCodeElementsWithRelationsByCodesUriAndVersion() throws Exception {
        getAllCodeElementsWithRelationsByCodesUriAndVersion("sisaltyysuhde2kanssa", 1)
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("/fixtures/resource/codeelement/withrelations.json")));
    }

    @Test
    void testGetAllCodeElementsByCodesUriAndVersionInvalid() throws Exception {
        getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", -1)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.validation.version")));
        getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", 0)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.validation.version")));
        getAllCodeElementsByCodesUriAndVersion("uridoesnotexist", 1)
                .andExpect(status().isNotFound())
                .andExpect(content().string("error.koodisto.not.found"));
    }

    @Test
    void testGetLatestCodeElementVersionsByCodeElementUri() throws Exception {
        getLatestCodeElementVersionsByCodeElementUri("montaversiota")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versio").value(3))
                .andExpect(jsonPath("$.koodiArvo").value("mv3"))
                .andExpect(jsonPath("$.koodiUri").value("montaversiota"))
                .andExpect(jsonPath("$.tila").value(Tila.HYVAKSYTTY.name()))
                .andExpect(jsonPath("$.koodisto.koodistoUri").value("lisaasisaltyy3"))
                .andExpect(jsonPath("$.metadata[0].nimi").value("Monta versiota 3"));
    }

    @Test
    void testGetLatestCodeElementVersionsByCodeElementUriInvalid() throws Exception {
        getLatestCodeElementVersionsByCodeElementUri("eioleolemassa").andExpect(status().isNotFound())
                .andExpect(content().string("error.koodi.not.found"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testInsertFailNameLengthValidation() throws Exception {
        JSONObject validDto = createValidCodeElementDtoJson("value", "*".repeat(FieldLengths.DEFAULT_FIELD_LENGTH * 2 + 1), "*".repeat(FieldLengths.DEFAULT_FIELD_LENGTH), 3);
        insert("inserttestkoodisto", validDto).andExpect(status().isBadRequest()).andExpect(content().string("nimi: size must be between 0 and 512"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testInsert() throws Exception {
        JSONObject validDto = createValidCodeElementDtoJson("value", "*".repeat(FieldLengths.DEFAULT_FIELD_LENGTH * 2), "*".repeat(FieldLengths.DEFAULT_FIELD_LENGTH), 3);
        insert("inserttestkoodisto", validDto)
                .andExpect(status().isCreated());

        getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.koodiArvo").value("value"));

        JSONObject validDto2 = createValidCodeElementDtoJson("value2", "Nimi2", 3);
        validDto2.put("voimassaAlkuPvm", "2014-01-01");
        validDto2.put("voimassaLoppuPvm", "2014-01-10");
        insert("inserttestkoodisto", validDto2)
                .andExpect(status().isCreated());

        getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value2")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.koodiArvo").value("value2"))
                .andExpect(jsonPath("$.voimassaLoppuPvm").value("2014-01-10"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testInsertInvalid() throws Exception {
        JSONObject validDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        insert(null, validDto).andExpect(status().isMethodNotAllowed()).andExpect(content().string("error.method.not.supported"));
        insert("", validDto).andExpect(status().isMethodNotAllowed()).andExpect(content().string("error.method.not.supported"));
        insert("totallyInvalidKoodistoUri", validDto).andExpect(status().isNotFound()).andExpect(content().string("error.koodisto.not.found"));

        insert("lisaasisaltyy3", null).andExpect(status().isBadRequest()).andExpect(content().string("error.http.message.not.readable"));
        insert("lisaasisaltyy3", new JSONObject()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.koodiarvo.empty")))
                .andExpect(content().string(containsString("error.metadata.empty")));

        JSONObject invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidDto.put("koodiArvo", "");
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.koodiarvo.empty")));

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidDto.put("koodiArvo", null);
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.koodiarvo.empty")));

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidDto.put("metadata", null);
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.metadata.empty")));

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        ArrayList<JSONObject> metadatas = new ArrayList<>();
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.metadata.empty")));

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidDto.put("voimassaLoppuPvm", "1970-01-01");
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error.validation.enddate")));

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        JSONObject invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", ""); // Invalid
        invalidMd.put("lyhytNimi", "Name");
        invalidMd.put("kuvaus", "Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", null); // Invalid
        invalidMd.put("lyhytNimi", "Name");
        invalidMd.put("kuvaus", "Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", "Name");
        invalidMd.put("lyhytNimi", ""); // Invalid
        invalidMd.put("kuvaus", "Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", "Name");
        invalidMd.put("lyhytNimi", null); // Invalid
        invalidMd.put("kuvaus", "Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", "Name");
        invalidMd.put("lyhytNimi", "Name");
        invalidMd.put("kuvaus", ""); // Invalid
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

        invalidDto = createValidCodeElementDtoJson("newdtouri", "Name", 3);
        invalidMd = new JSONObject();
        invalidMd.put("kieli", Kieli.FI);
        invalidMd.put("nimi", "Name");
        invalidMd.put("lyhytNimi", "Name");
        invalidMd.put("kuvaus", null); // Invalid
        metadatas.add(invalidMd);
        invalidDto.put("metadata", new JSONArray(metadatas));
        insert("lisaasisaltyy3", invalidDto).andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testAddRelation() throws Exception {
        String codeElementUri = "lisaarinnasteinen14";
        Assertions.assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        addRelation(codeElementUri, "lisaarinnasteinen14kanssa1", "RINNASTEINEN").andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        String codeElementUri2 = "lisaasisaltyy18";
        Assertions.assertEquals(0, service.listByRelation(codeElementUri2, 1, false, SuhteenTyyppi.SISALTYY).size());
        addRelation(codeElementUri2, "lisaasisaltyy18kanssa1", "SISALTYY").andExpect(status().isOk());
        Assertions.assertEquals(1, service.listByRelation(codeElementUri2, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testAddRelationInvalid() throws Exception {
        String codeElementUri = "lisaarinnasteinen14";
        String codeElementUriToAdd = "lisaarinnasteinen14kanssa1";
        String codeElementUriToAddWithoutCodesRelation = "sisaltyysuhde4kanssa1";
        String relationType = "RINNASTEINEN";

        addRelation(codeElementUri, codeElementUriToAdd, "doenostexist").andExpect(status().isBadRequest()).andExpect(content().string("error.validation.relationtype"));
        addRelation(codeElementUri, codeElementUri, relationType).andExpect(status().isBadRequest()).andExpect(content().string("error.codeelement.relation.to.self"));
        addRelation("doenotexist", codeElementUriToAdd, relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        addRelation(codeElementUri, "doesnotexist", relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        addRelation(codeElementUri, codeElementUriToAddWithoutCodesRelation, relationType).andExpect(status().isBadRequest()).andExpect(content().string("error.codeelement.codes.have.no.relation"));

        Assertions.assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "lisaasisaltyy18";
        codeElementUriToAdd = "lisaasisaltyy18kanssa1";
        codeElementUriToAddWithoutCodesRelation = "sisaltyysuhde4kanssa1";
        relationType = "SISALTYY";

        addRelation(codeElementUri, codeElementUriToAdd, "doenostexist").andExpect(status().isBadRequest()).andExpect(content().string("error.validation.relationtype"));
        addRelation(codeElementUri, codeElementUri, relationType).andExpect(status().isBadRequest()).andExpect(content().string("error.codeelement.relation.to.self"));
        addRelation("doenotexist", codeElementUriToAdd, relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        addRelation(codeElementUri, "doesnotexist", relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        addRelation(codeElementUri, codeElementUriToAddWithoutCodesRelation, relationType).andExpect(status().isBadRequest()).andExpect(content().string("error.codeelement.codes.have.no.relation"));

        Assertions.assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testAddRelationToSameCodes() throws Exception {
        String codeElementUri = "lisaasisaltyy18kanssa1";
        String codeElementUriToAdd = "lisaasisaltyy18kanssa2";
        String relationType = "SISALTYY";

        addRelation(codeElementUri, codeElementUriToAdd, relationType).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementWithNameAndRelationsToSameCodesChanges() throws Exception {
        String koodiUri = "savekoodisuhteillaomaankoodistoon";
        String nimi = "uusinimi";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.includesCodeElements.length()").value(1))
                .andExpect(jsonPath("$.includesCodeElements[0].codeElementUri").value("samankoodistonsisalla1"))
                .andExpect(jsonPath("$.withinCodeElements.length()").value(1))
                .andExpect(jsonPath("$.withinCodeElements[0].codeElementUri").value("samankoodistonsisalla2")).andReturn().getResponse().getContentAsString());
        JSONArray metadata = codeElementToBeSaved.getJSONArray("metadata");
        metadata.put(0, metadata.getJSONObject(0).put("nimi", nimi));

        codeElementToBeSaved.put("metadata", metadata);
        codeElementToBeSaved.put("includesCodeElements", new JSONArray(List.of(new JSONObject(Map.of("codeElementUri", "uusisamankoodistonsisalla1", "codeElementVersion", 1, "passive", "false")))));
        codeElementToBeSaved.put("withinCodeElements", new JSONArray(List.of(new JSONObject(Map.of("codeElementUri", "uusisamankoodistonsisalla2", "codeElementVersion", 1, "passive", "false")))));
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio + 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata[0].nimi").value(nimi))
                .andExpect(jsonPath("$.includesCodeElements.length()").value(2))
                .andExpect(jsonPath("$.withinCodeElements.length()").value(2))
                .andExpect(content().json("{\"includesCodeElements\":[{\"passive\": false,\"codeElementUri\":\"uusisamankoodistonsisalla1\"},{\"passive\": true,\"codeElementUri\":\"samankoodistonsisalla1\"}]}"))
                .andExpect(content().json("{\"withinCodeElements\":[{\"passive\": false,\"codeElementUri\":\"uusisamankoodistonsisalla2\"},{\"passive\": true,\"codeElementUri\":\"samankoodistonsisalla2\"}]}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testRemoveRelation() throws Exception {
        String codeElementUri = "sisaltaakoodisto1koodit";
        Assertions.assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        removeRelation(codeElementUri, "rinnastuu4kanssa1", "RINNASTEINEN");
        Assertions.assertEquals(2, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "sisaltaakoodisto1koodit";
        Assertions.assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        removeRelation(codeElementUri, "sisaltyysuhde4kanssa1", "SISALTYY");
        Assertions.assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testRemoveRelationInvalid() throws Exception {
        String codeElementUri = "sisaltaakoodisto1koodit";
        String codeElementUriToRemove = "rinnastuu4kanssa1";
        String relationType = "RINNASTEINEN";
        removeRelation(codeElementUri, codeElementUriToRemove, "doenostexist").andExpect(status().isBadRequest());

        removeRelation("doenotexist", codeElementUriToRemove, relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        removeRelation(codeElementUri, "doesnotexist", relationType).andExpect(status().isNotFound()).andExpect(content().string("error.codeelement.relation.list.empty"));

        Assertions.assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "sisaltaakoodisto1koodit";
        codeElementUriToRemove = "sisaltyysuhde4kanssa1";
        relationType = "SISALTYY";

        removeRelation("doenotexist", codeElementUriToRemove, relationType).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        removeRelation(codeElementUri, "doesnotexist", relationType).andExpect(status().isNotFound()).andExpect(content().string("error.codeelement.relation.list.empty"));

        Assertions.assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testDelete() throws Exception {
        String codeElementUri = "tuhottavatestikoodi";
        int codeElementVersion = 1;
        getCodeElementByUriAndVersion(codeElementUri, codeElementVersion).andExpect(status().isOk());
        delete(codeElementUri, codeElementVersion).andExpect(status().isAccepted());
        getCodeElementByUriAndVersion(codeElementUri, codeElementVersion).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testDeleteInvalid() throws Exception {
        delete("tuhottavatestikoodi", 0).andExpect(status().isBadRequest()).andExpect(content().string(containsString("error.validation.version")));
        delete("tuhottavatestikoodi", -1).andExpect(status().isBadRequest()).andExpect(content().string(containsString("error.validation.version")));
        delete("thisisnotexistinguri", 1).andExpect(status().isNotFound()).andExpect(content().string("error.koodi.not.found"));
        delete("sisaltaakoodisto1koodit", 1).andExpect(status().isBadRequest()).andExpect(content().string("error.codeelement.not.passive"));
        getCodeElementByUriAndVersion("tuhottavatestikoodi", 1).andExpect(status().isOk()).andExpect(content().json("{}"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testUpdate() throws Exception {
        JSONObject original = new JSONObject(
                getCodeElementByCodeElementUri("updatetestkoodisto", 1, "paivitettavatestikoodi")
                        .andExpect(status().isOk())
                        .andExpect(content().json("{}"))
                        .andExpect(jsonPath("$.metadata.length()").value(1))
                        .andReturn().getResponse().getContentAsString());
        JSONArray metadata = original.getJSONArray("metadata");
        metadata.put(0, metadata.getJSONObject(0).put("nimi", "Modified Name"));
        original.put("metadata", metadata);
        update(original)
                .andExpect(status().isCreated());

        getCodeElementByCodeElementUri("updatetestkoodisto", 2, "paivitettavatestikoodi")
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(jsonPath("$.metadata.length()").value(1))
                .andExpect(jsonPath("$.metadata[0].nimi").value("Modified Name"));
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void testUpdateInvalid() throws Exception {
        update(null).andExpect(status().isBadRequest()).andExpect(content().string("error.http.message.not.readable"));
    }

    @Test
    void returnsChangesForCodeElement() throws Exception {
        getChangesToCodeElement("montaversiota", 1, false)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.muutosTila").value(MuutosTila.MUUTOKSIA.name()),
                        jsonPath("$.muuttuneetTiedot[0].nimi").value("Monta versiota 3"),
                        jsonPath("$.voimassaAlkuPvm").isEmpty(),
                        jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void returnsNoChangesForCodeElement() throws Exception {
        getChangesToCodeElement("montaversiota", 3, false).andExpectAll(
                status().isOk(),
                jsonPath("$.muutosTila").value(MuutosTila.EI_MUUTOKSIA.name()),
                jsonPath("$.muuttuneetTiedot").isEmpty(),
                jsonPath("$.voimassaAlkuPvm").isEmpty(),
                jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void comparesAgainstLatestAcceptedCodeElementVersion() throws Exception {
        getChangesToCodeElement("viimeinenonluonnos", 1, true).andExpectAll(
                status().isOk(),
                jsonPath("$.tila").doesNotExist(),
                jsonPath("$.viimeisinVersio").value(2));
    }

    @Test
    void comparesAgainstLatestCodeElementVersion() throws Exception {
        getChangesToCodeElement("viimeinenonluonnos", 1, false).andExpectAll(
                status().isOk(),
                jsonPath("$.tila").value(Tila.LUONNOS.name()),
                jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void comparesAgainstLatestAcceptedCodeElementVersionUsingDate() throws Exception {
        getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 1, 2000, 0, 0, 0, true).andExpectAll(
                status().isOk(),
                jsonPath("$.tila").doesNotExist(),
                jsonPath("$.viimeisinVersio").value(2));
    }

    @Test
    void comparesAgainstLatestCodeElementVersionUsingDate() throws Exception {
        getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 12, 2000, 0, 0, 0, false).andExpectAll(
                status().isOk(),
                jsonPath("$.tila").value(Tila.LUONNOS.name()),
                jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void returnsChangesForCodeElementUsingDateFromPast() throws Exception {
        getChangesToCodeElementWithDate("montaversiota", 12, 1, 2000, 0, 0, 0, false).andExpectAll(
                status().isOk(),
                jsonPath("$.muutosTila").value(MuutosTila.MUUTOKSIA.name()),
                jsonPath("$.muuttuneetTiedot[0].nimi").value("Monta versiota 3"),
                jsonPath("$.voimassaAlkuPvm").isEmpty(),
                jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void returnsNoChangesForCodeElementUsingDateFromFuture() throws Exception {
        getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, 0, 0, false).andExpectAll(
                status().isOk(),
                jsonPath("$.muutosTila").value(MuutosTila.EI_MUUTOKSIA.name()),
                jsonPath("$.muuttuneetTiedot").isEmpty(),
                jsonPath("$.viimeisinVersio").value(3));
    }

    @Test
    void returnsCodeElementHasBeenRemovedWhenItIsNotFoundInLatestCodesVersion() throws Exception {
        getChangesToCodeElement("poistettu", 1, false).andExpectAll(
                status().isOk(),
                jsonPath("$.muutosTila").value(MuutosTila.POISTETTU.name())
        );
    }

    @Test
    void returnsBadRequestWhenVersionNumberIsZeroForQueryingCodeElementChanges() throws Exception {
        getChangesToCodeElement("poistettu", 0, false).andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForBadDateParametersWhenQueryingCodeElementChanges() throws Exception {
        getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, 0, -1, false).andExpect(status().isBadRequest());
        getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, -1, 0, false).andExpect(status().isBadRequest());
        getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 25, 0, 0, false).andExpect(status().isBadRequest());
        getChangesToCodeElementWithDate("montaversiota", 32, 1, 4000, 0, 0, 0, false).andExpect(status().isBadRequest());
        getChangesToCodeElementWithDate("montaversiota", 12, 15, 4000, 0, 0, 0, true).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementWithNewName() throws Exception {
        String koodiUri = "sisaltyysuhde4kanssa1";
        String nimi = "uusinimi";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.tila").value(Tila.HYVAKSYTTY.name()),
                        jsonPath("$.metadata[0].nimi").value("Alajaervi")
                ).andReturn().getResponse().getContentAsString());

        JSONArray metadata = codeElementToBeSaved.getJSONArray("metadata");
        metadata.put(0, metadata.getJSONObject(0).put("nimi", nimi));
        codeElementToBeSaved.put("metadata", metadata);
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio + 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.tila").value(Tila.LUONNOS.name()),
                        jsonPath("$.metadata[0].nimi").value(nimi)
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementWithNewNameAndRelations() throws Exception {
        String koodiUri = "savekoodineljallasuhteella";
        String nimi = "uusinimi";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(1),
                        jsonPath("$.levelsWithCodeElements.length()").value(2),
                        jsonPath("$.withinCodeElements.length()").value(1)
                ).andReturn().getResponse().getContentAsString());

        JSONArray metadata = codeElementToBeSaved.getJSONArray("metadata");
        metadata.put(0, metadata.getJSONObject(0).put("nimi", nimi));
        JSONArray includesCodeElements = codeElementToBeSaved.getJSONArray("includesCodeElements");
        includesCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde1", "codeElementVersion", 1, "passive", "false")));
        JSONArray levelsWithCodeElements = codeElementToBeSaved.getJSONArray("levelsWithCodeElements");
        levelsWithCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde2", "codeElementVersion", 1, "passive", "false")));
        JSONArray withinCodeElements = codeElementToBeSaved.getJSONArray("withinCodeElements");
        withinCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde3", "codeElementVersion", 1, "passive", "false")));
        codeElementToBeSaved.put("metadata", metadata);
        codeElementToBeSaved.put("includesCodeElements", includesCodeElements);
        codeElementToBeSaved.put("levelsWithCodeElements", levelsWithCodeElements);
        codeElementToBeSaved.put("withinCodeElements", withinCodeElements);
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio + 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(2),
                        jsonPath("$.levelsWithCodeElements.length()").value(3),
                        jsonPath("$.withinCodeElements.length()").value(3)
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementRelationIncludes() throws Exception {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        JSONArray includesCodeElements = codeElementToBeSaved.getJSONArray("includesCodeElements");
        includesCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde1", "codeElementVersion", 1, "passive", "false")));
        codeElementToBeSaved.put("includesCodeElements", includesCodeElements);
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio + 1)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(2),
                        jsonPath("$.withinCodeElements.length()").value(1),
                        jsonPath("$.levelsWithCodeElements.length()").value(2)
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementRelationWithin() throws Exception {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        JSONArray withinCodeElements = codeElementToBeSaved.getJSONArray("withinCodeElements");
        withinCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde3", "codeElementVersion", 1, "passive", "false")));
        codeElementToBeSaved.put("withinCodeElements", withinCodeElements);
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(1),
                        jsonPath("$.withinCodeElements.length()").value(3),
                        jsonPath("$.levelsWithCodeElements.length()").value(2)
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementRelationLevelsWith() throws Exception {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        JSONArray levelsWithCodeElements = codeElementToBeSaved.getJSONArray("levelsWithCodeElements");
        levelsWithCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde2", "codeElementVersion", 1, "passive", "false")));
        codeElementToBeSaved.put("levelsWithCodeElements", levelsWithCodeElements);
        save(codeElementToBeSaved).andExpect(status().isOk());

        getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(1),
                        jsonPath("$.withinCodeElements.length()").value(1),
                        jsonPath("$.levelsWithCodeElements.length()").value(3)
                );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementWithAllRelationChanges() throws Exception {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(1),
                        jsonPath("$.levelsWithCodeElements.length()").value(2),
                        jsonPath("$.withinCodeElements.length()").value(1)
                ).andReturn().getResponse().getContentAsString());
        codeElementToBeSaved.put("includesCodeElements", new JSONArray(List.of(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde1", "codeElementVersion", 1, "passive", "false")))));
        codeElementToBeSaved.put("levelsWithCodeElements", new JSONArray(List.of(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde2", "codeElementVersion", 1, "passive", "false")))));
        codeElementToBeSaved.put("withinCodeElements", new JSONArray(List.of(new JSONObject(Map.of("codeElementUri", "uusisavekoodinsuhde3", "codeElementVersion", 1, "passive", "false")))));
        getCodeElementByUriAndVersion(koodiUri, versio).andExpectAll(
                status().isOk(),
                content().json("{\"includesCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde1\"}]}"),
                content().json("{\"levelsWithCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde3\"},{\"codeElementUri\": \"savekoodinsuhde2\"}]}"),
                content().json("{\"withinCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde4\"}]}")
        );
        save(codeElementToBeSaved).andExpectAll(
                status().isOk(),
                content().string("2")

        );

        getCodeElementByUriAndVersion(koodiUri, versio).andExpectAll(
                status().isOk(),
                content().json("{\"includesCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde1\"}]}"),
                content().json("{\"levelsWithCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde3\"},{\"codeElementUri\": \"savekoodinsuhde2\"}]}"),
                content().json("{\"withinCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde4\"}]}")
        );
        getCodeElementByUriAndVersion(koodiUri, versio + 1).andExpectAll(
                status().isOk(),
                content().json("{\"includesCodeElements\":[{\"codeElementUri\": \"uusisavekoodinsuhde1\"}]}"),
                content().json("{\"levelsWithCodeElements\":[{\"codeElementUri\": \"uusisavekoodinsuhde2\"}]}"),
                content().json("{\"withinCodeElements\":[{\"codeElementUri\": \"savekoodinsuhde4\",\"passive\": true},{\"codeElementUri\": \"uusisavekoodinsuhde3\",\"passive\": false}]}")
        );
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    void savesCodeElementRelationChangesIfRelationHasExistedBefore() throws Exception {
        String koodiUri = "uusirelaatiovanhantilalle1";
        int versio = 1;

        JSONObject codeElementToBeSaved = new JSONObject(getCodeElementByUriAndVersion(koodiUri, versio)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.includesCodeElements.length()").value(1),
                        jsonPath("$.includesCodeElements.[0].passive").value(true),
                        jsonPath("$.levelsWithCodeElements.length()").value(0),
                        jsonPath("$.withinCodeElements.length()").value(0)
                ).andReturn().getResponse().getContentAsString());


        JSONArray includesCodeElements = codeElementToBeSaved.getJSONArray("includesCodeElements");
        includesCodeElements.put(new JSONObject(Map.of("codeElementUri", "uusirelaatiovanhantilalle2", "codeElementVersion", 1, "passive", "false")));
        codeElementToBeSaved.put("includesCodeElements", includesCodeElements);
        save(codeElementToBeSaved).andExpect(status().isOk());


        getCodeElementByUriAndVersion(koodiUri, versio + 1).andExpectAll(
                status().isOk(),
                jsonPath("$.includesCodeElements.length()").value(1),
                jsonPath("$.includesCodeElements.[0].passive").value(false),
                jsonPath("$.includesCodeElements.[0].codeElementUri").value("uusirelaatiovanhantilalle2"),
                jsonPath("$.levelsWithCodeElements.length()").value(0),
                jsonPath("$.withinCodeElements.length()").value(0)
        );
    }


    private ResultActions addRelations(String codeElementUri, String st, JSONObject ob) throws Exception {
        ob.put("codeElementUri", codeElementUri);
        ob.put("relationType", st);
        return mockMvc.perform(post(BASE_PATH + "/addrelations").contentType(MediaType.APPLICATION_JSON).content(ob.toString()));
    }

    private ResultActions removeRelations(String codeElementUri, String st, JSONObject ob) throws Exception {
        ob.put("codeElementUri", codeElementUri);
        ob.put("relationType", st);
        return mockMvc.perform(post(BASE_PATH + "/removerelations").contentType(MediaType.APPLICATION_JSON).content(ob.toString()));
    }

    private ResultActions getAllCodeElementVersionsByCodeElementUri(String uri) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/{codeElementUri}", uri));
    }

    private ResultActions getCodeElementByUriAndVersion(String codeElementUri, int codeElementVersion) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/{codeElementUri}/{codeElementVersion}", codeElementUri, codeElementVersion));
    }

    private ResultActions getCodeElementByCodeElementUri(String codesUri, int codeElementVersion, String codeElementUri) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/{codesUri}/{codesVersion}/{codeElementUri}", codesUri, codeElementVersion, codeElementUri));
    }

    private ResultActions getAllCodeElementsByCodesUriAndVersion(String codesUri, int codesVersion) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/codes/{codesUri}/{codesVersion}", codesUri, codesVersion));
    }

    private ResultActions getAllCodeElementsWithRelationsByCodesUriAndVersion(String codesUri, int codesVersion) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/codes/withrelations/{codesUri}/{codesVersion}", codesUri, codesVersion));
    }

    private ResultActions getAllCodeElementsByCodesUri(String codesUri) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/codes/{codesUri}", codesUri));
    }

    private ResultActions getLatestCodeElementVersionsByCodeElementUri(String codeElementUri) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/latest/{codeElementUri}", codeElementUri));
    }

    private ResultActions getChangesToCodeElement(String codeElementUri, int codeElementVersion, boolean compareToLatestAccepted) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/changes/{codeElementUri}/{codeElementVersion}", codeElementUri, codeElementVersion).param("compareToLatestAccepted", Boolean.toString(compareToLatestAccepted)));
    }

    private ResultActions getChangesToCodeElementWithDate(String codeElementUri, int dayofmonth, int month, int year, int hour, int minute, int second, boolean compareToLatestAccepted) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/changes/withdate/{codeElementUri}/{dayofmonth}/{month}/{year}/{hour}/{minute}/{second}", codeElementUri, dayofmonth, month, year, hour, minute, second).param("compareToLatestAccepted", Boolean.toString(compareToLatestAccepted)));
    }

    private ResultActions insert(String codesUri, JSONObject o) throws Exception {
        return mockMvc.perform(post(BASE_PATH + "/{codesUri}", codesUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(o != null ? o.toString() : ""));
    }

    private ResultActions save(JSONObject o) throws Exception {
        return mockMvc.perform(put(BASE_PATH + "/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(o != null ? o.toString() : ""));
    }

    private ResultActions update(JSONObject o) throws Exception {
        return mockMvc.perform(put(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(o != null ? o.toString() : ""));
    }

    private ResultActions delete(String codeElementUri, int codeElementVersion) throws Exception {
        return mockMvc.perform(post(BASE_PATH + "/delete/{codeElementUri}/{codeElementVersion}", codeElementUri, codeElementVersion)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions addRelation(String codeElementUri, String codeElementUriToAdd, String relationType) throws Exception {
        return mockMvc.perform(post(BASE_PATH + "/addrelation/{codeElementUri}/{codeElementUriToAdd}/{relationType}", codeElementUri, codeElementUriToAdd, relationType)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions removeRelation(String codeElementUri, String codeElementUriToRemove, String relationType) throws Exception {
        return mockMvc.perform(post(BASE_PATH + "/removerelation/{codeElementUri}/{codeElementUriToRemove}/{relationType}", codeElementUri, codeElementUriToRemove, relationType)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private JSONObject createValidCodeElementDtoJson(String value, String name, int amountOfMetadatas) throws JSONException {
        return createValidCodeElementDtoJson(value, name, name, amountOfMetadatas);
    }

    private JSONObject createValidCodeElementDtoJson(String value, String name, String shortName, int amountOfMetadatas) throws JSONException {
        JSONObject o = new JSONObject();


        o.put("voimassaAlkuPvm", "2022-05-05");
        o.put("voimassaLoppuPvm", null);
        o.put("koodiArvo", value);
        List<JSONObject> mds = new ArrayList<>();
        for (int i = 0; i < amountOfMetadatas; i++) {
            JSONObject md = new JSONObject();
            md.put("kieli", Kieli.values()[i % Kieli.values().length]);
            md.put("nimi", name);
            md.put("lyhytNimi", shortName);
            md.put("kuvaus", "Kuvaus");
            mds.add(md);
        }
        o.put("metadata", new JSONArray(mds));
        return o;
    }
    private String readFile(String fileName) throws Exception {
        return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource(fileName)).toURI()), StandardCharsets.UTF_8);
    }
}
