package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.HashSet;

import javax.ws.rs.core.Response;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;


import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data-codes-rest.xml")
public class CodesGroupResourceTest {

    @Autowired
    private CodesGroupResource resource;

    @Test
    public void testGetCodesByCodesUri() {
        Response response = resource.getCodesByCodesUri(-1L);
        assertResponse(response, 200);
        KoodistoRyhmaDto dto = (KoodistoRyhmaDto) response.getEntity();
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
    public void testUpdate() {
        Response response = resource.getCodesByCodesUri(-4L);
        assertResponse(response, 200);
        KoodistoRyhmaDto dto = (KoodistoRyhmaDto) response.getEntity();
        changename(dto, "paivitettunimi");

        Response updateResponse = resource.update(dto);
        assertResponse(updateResponse, 201);

        response = resource.getCodesByCodesUri(-4L);
        assertResponse(response, 200);
        assertDtoEquals(dto, (KoodistoRyhmaDto) response.getEntity());

    }
    
    @Test
    public void testUpdateInvalid() {
        assertResponse(resource.update(null), 400, "error.validation.codesgroup");
        assertResponse(resource.update(new KoodistoRyhmaDto()), 400, "error.codesgroup.uri.empty");
        assertResponse(resource.update(createDto("totallyvaliduri", 0)), 400, "error.metadata.empty");
        assertResponse(resource.update(createDto("", 3)), 400, "error.codesgroup.uri.empty");
    }

    @Test
    public void testInsert() {
        String newName = "newnameforcodesgroup";
        KoodistoRyhmaDto dto = createDto(newName, 1);
        Response insert = resource.insert(dto);
        assertResponse(insert, 201);
        KoodistoRyhmaDto result = (KoodistoRyhmaDto) insert.getEntity();
        Long newId = result.getId();

        Response codesByCodesUri = resource.getCodesByCodesUri(newId);
        assertResponse(codesByCodesUri, 200);
        KoodistoRyhmaDto insertedDto = (KoodistoRyhmaDto) codesByCodesUri.getEntity();
        assertEquals("http://" + newName, insertedDto.getKoodistoRyhmaUri());
        assertEquals(1, insertedDto.getKoodistoRyhmaMetadatas().size());
        assertEquals(0, insertedDto.getKoodistos().size());
    }
    
    @Test
    public void testInsertInvalid() {
        assertResponse(resource.insert(null), 400, "error.validation.codesgroup");
        assertResponse(resource.insert(new KoodistoRyhmaDto()), 400, "error.metadata.empty");
        assertResponse(resource.insert(createDto("totallyvaliduri", 0)), 400, "error.metadata.empty");
        assertResponse(resource.insert(createDto("", 3)), 400, "error.validation.metadata");
    }

    @Test
    public void testDelete() {
        assertResponse(resource.getCodesByCodesUri(-3L), 200);
        assertResponse(resource.delete(-3L), 202);
        assertResponse(resource.getCodesByCodesUri(-3L), 500);
    }
    
    @Test
    public void testDeleteInvalid() {
        assertResponse(resource.delete(null), 400, "error.validation.id");
        assertResponse(resource.delete(0L), 500, "error.codesgroup.not.found");
        assertResponse(resource.delete(99999L), 500, "error.codesgroup.not.found");
    }

    // UTILITIES
    // /////////

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }
    
    private void assertResponse(Response response, int expectedStatus, Object expectedEntity) {
        assertResponse(response, expectedStatus);
        assertEquals(expectedEntity, response.getEntity());
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
