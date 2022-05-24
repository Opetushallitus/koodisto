package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.resource.CodesGroupResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_READ_UPDATE;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:test-data-codes-rest.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:truncate_tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class CodesGroupResourceTest {

    @Autowired
    private CodesGroupResource resource;

    @Test
    @Transactional
    public void testGetCodesByCodesUri() {
        ResponseEntity response = resource.getCodesByCodesUri(-1L);
        assertResponse(response, 200);
        KoodistoRyhmaDto dto = (KoodistoRyhmaDto) response.getBody();
        assertEquals("relaatioidenlisaaminen", dto.getKoodistoRyhmaUri());
        assertEquals(1, dto.getKoodistoRyhmaMetadatas().size());
        assertEquals(5, dto.getKoodistos().size());
    }

    @Test
    public void testGetCodesByCodesUriInvalid() {
        assertResponse(resource.getCodesByCodesUri(0L), 500, "error.codesgroup.not.found");
        assertResponse(resource.getCodesByCodesUri(null), 400, "error.validation.id");
        assertResponse(resource.getCodesByCodesUri(99999L), 500, "error.codesgroup.not.found");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_APP_KOODISTO_CRUD")
    public void testUpdate() {
        ResponseEntity response = resource.getCodesByCodesUri(-4L);
        assertResponse(response, 200);
        KoodistoRyhmaDto dto = (KoodistoRyhmaDto) response.getBody();
        changename(dto, "paivitettunimi");

        ResponseEntity updateResponse = resource.update(dto);
        assertResponse(updateResponse, 201);

        response = resource.getCodesByCodesUri(-4L);
        assertResponse(response, 200);
        assertDtoEquals(dto, (KoodistoRyhmaDto) response.getBody());

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = ROLE_APP_KOODISTO_READ_UPDATE)
    public void testUpdateInvalid() {
        assertResponse(resource.update(null), 400, "error.validation.codesgroup");
        assertResponse(resource.update(new KoodistoRyhmaDto()), 400, "error.codesgroup.uri.empty");
        assertResponse(resource.update(createDto("totallyvaliduri", 0)), 400, "error.metadata.empty");
        assertResponse(resource.update(createDto("", 3)), 400, "error.codesgroup.uri.empty");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = "ROLE_APP_KOODISTO_CRUD")
    public void testInsert() {
        String newName = "newnameforcodesgroup";
        KoodistoRyhmaDto dto = createDto(newName, 1);
        ResponseEntity insert = resource.insert(dto);
        assertResponse(insert, 201);
        KoodistoRyhmaDto result = (KoodistoRyhmaDto) insert.getBody();
        Long newId = result.getId();

        ResponseEntity codesByCodesUri = resource.getCodesByCodesUri(newId);
        assertResponse(codesByCodesUri, 200);
        KoodistoRyhmaDto insertedDto = (KoodistoRyhmaDto) codesByCodesUri.getBody();
        assertEquals(newName, insertedDto.getKoodistoRyhmaUri());
        assertEquals(1, insertedDto.getKoodistoRyhmaMetadatas().size());
        assertEquals(0, insertedDto.getKoodistos().size());
    }

    @Test
    @WithMockUser(authorities = "ROLE_APP_KOODISTO_CRUD")
    public void testInsertInvalid() {
        assertResponse(resource.insert(null), 400, "error.validation.codesgroup");
        assertResponse(resource.insert(new KoodistoRyhmaDto()), 400, "error.metadata.empty");
        assertResponse(resource.insert(createDto("totallyvaliduri", 0)), 400, "error.metadata.empty");
        assertResponse(resource.insert(createDto("", 3)), 400, "error.validation.metadata");
    }

    @Test
    @WithMockUser(authorities = "ROLE_APP_KOODISTO_CRUD")
    public void testDelete() {
        assertResponse(resource.getCodesByCodesUri(-3L), 200);
        assertResponse(resource.delete(-3L), 202);
        assertResponse(resource.getCodesByCodesUri(-3L), 500);
    }

    @Test
    @WithMockUser(authorities = "ROLE_APP_KOODISTO_CRUD")
    public void testDeleteInvalid() {
        assertResponse(resource.delete(null), 400, "error.validation.id");
        assertResponse(resource.delete(0L), 500, "error.codesgroup.not.found");
        assertResponse(resource.delete(99999L), 500, "error.codesgroup.not.found");
    }

    // UTILITIES
    // /////////

    private void assertResponse(ResponseEntity response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCodeValue());
    }

    private void assertResponse(ResponseEntity response, int expectedStatus, Object expectedEntity) {
        assertResponse(response, expectedStatus);
        assertEquals(expectedEntity, response.getBody());
    }

    private KoodistoRyhmaDto createDto(String name, int howManyMetadatas) {
        KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
        HashSet<KoodistoRyhmaMetadata> metadatas = new HashSet<KoodistoRyhmaMetadata>();
        for (int i = 0; i < howManyMetadatas; i++) {
            KoodistoRyhmaMetadata md = new KoodistoRyhmaMetadata();
            md.setKieli(Kieli.values()[i % Kieli.values().length]);
            md.setNimi(name);
            metadatas.add(md);
        }
        dto.setKoodistoRyhmaMetadatas(metadatas);
        dto.setKoodistoRyhmaUri(name);
        return dto;
    }

    private void changename(KoodistoRyhmaDto dto, String name) {
        for (KoodistoRyhmaMetadata existingmd : dto.getKoodistoRyhmaMetadatas()) {
            existingmd.setNimi(name);
        }
    }

    private void assertDtoEquals(KoodistoRyhmaDto dto, KoodistoRyhmaDto dto2) {
        assertEquals(dto.getKoodistoRyhmaMetadatas(), dto2.getKoodistoRyhmaMetadatas());
        assertEquals(dto.getKoodistoRyhmaUri(), dto2.getKoodistoRyhmaUri());
        assertEquals(dto.getKoodistos(), dto2.getKoodistos());

    }
}
