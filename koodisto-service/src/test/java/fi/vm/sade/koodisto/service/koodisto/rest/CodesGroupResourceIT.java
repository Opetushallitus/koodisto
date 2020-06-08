package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.HashSet;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.test.support.ResponseStatusExceptionMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;


import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static fi.vm.sade.koodisto.test.support.Assertions.assertException;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
/*@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })*/
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data-codes-rest.xml")
public class CodesGroupResourceIT {

    @Autowired
    private CodesGroupResource resource;

    @Test
    public void testGetCodesByCodesUri() {
        KoodistoRyhmaDto dto = resource.getCodesByCodesUri(-1L);
        assertEquals("relaatioidenlisaaminen", dto.getKoodistoRyhmaUri());
        assertEquals(1, dto.getKoodistoRyhmaMetadatas().size());
        assertEquals(5, dto.getKoodistos().size());
    }

    @Test
    public void testGetCodesByCodesUriInvalid() {
        assertException(() -> resource.getCodesByCodesUri(0L),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codesgroup.not.found"));
        assertException(() -> resource.getCodesByCodesUri(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.id"));
        assertException(() -> resource.getCodesByCodesUri(99999L),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codesgroup.not.found"));
    }

    @Test
    public void testUpdate() {
        KoodistoRyhmaDto dto = resource.getCodesByCodesUri(-4L);
        changename(dto, "paivitettunimi");

        resource.update(dto);
        assertDtoEquals(dto, resource.getCodesByCodesUri(-4L));

    }
    
    @Test
    public void testUpdateInvalid() {
        assertException(() -> resource.update(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesgroup"));
        assertException(() -> resource.update(new KoodistoRyhmaDto()),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.codesgroup.uri.empty"));
        assertException(() -> resource.update(createDto("totallyvaliduri", 0)),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.metadata.empty"));
        assertException(() -> resource.update(createDto("", 3)),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.codesgroup.uri.empty"));
    }

    @Test
    public void testInsert() {
        String newName = "newnameforcodesgroup";
        KoodistoRyhmaDto dto = createDto(newName, 1);
        KoodistoRyhmaDto result = resource.insert(dto);
        Long newId = result.getId();

        KoodistoRyhmaDto insertedDto = resource.getCodesByCodesUri(newId);
        assertEquals("http://" + newName, insertedDto.getKoodistoRyhmaUri());
        assertEquals(1, insertedDto.getKoodistoRyhmaMetadatas().size());
        assertEquals(0, insertedDto.getKoodistos().size());
    }
    
    @Test
    public void testInsertInvalid() {
        assertException(() -> resource.insert(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesgroup"));
        assertException(() -> resource.insert(new KoodistoRyhmaDto()),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.metadata.empty"));
        assertException(() -> resource.insert(createDto("totallyvaliduri", 0)),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.metadata.empty"));
        assertException(() -> resource.insert(createDto("", 3)),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.metadata"));
    }

    @Test
    public void testDelete() {
        resource.getCodesByCodesUri(-3L);
        resource.delete(-3L);
        assertException(() -> resource.getCodesByCodesUri(-3L),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    
    @Test
    public void testDeleteInvalid() {
        assertException(() -> resource.delete(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.id"));
        assertException(() -> resource.delete(0L),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codesgroup.not.found"));
        assertException(() -> resource.delete(99999L),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codesgroup.not.found"));
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
