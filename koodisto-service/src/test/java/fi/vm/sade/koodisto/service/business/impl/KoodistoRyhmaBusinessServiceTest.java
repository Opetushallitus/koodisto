package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:test-data-codes-rest.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:truncate_tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
public class KoodistoRyhmaBusinessServiceTest {

    @Autowired
    private KoodistoRyhmaBusinessService resource;

    @Test
    public void testCreateKoodistoRyhma() {
        KoodistoRyhmaDto dto = createRyhma("createtest");
        KoodistoRyhma group = resource.createKoodistoRyhma(dto);
        assertNotNull(group);
        assertEquals("createtest", group.getKoodistoRyhmaUri());
        assertEquals(1, group.getKoodistoJoukkoMetadatas().size());
        assertEquals(0, group.getKoodistos().size());
    }

    @Test
    public void testCreateInvalidKoodistoRyhmaFails() {
        KoodistoRyhma group = null;
        try {
            group = resource.createKoodistoRyhma(null);
            fail("Null DTO accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);

        try {
            KoodistoRyhmaDto dto = createRyhma("relaatioidenlisaaminen");
            group = resource.createKoodistoRyhma(dto);
            fail("Same koodistoryhma uri accepted.");
        } catch (DataIntegrityViolationException e) { // This was persistenceError in previous spring, hope it is ok.

        }
        assertNull(group);

        try {
            KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
            dto.setKoodistoRyhmaUri(null);
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä without uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);

        try {
            KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
            dto.setKoodistoRyhmaUri("");
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä with empty uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);

        try {
            KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>());
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä without metadata accepted.");
        } catch (MetadataEmptyException e) {
        }
        assertNull(group);
        
        try {
            KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
            Set<KoodistoRyhmaMetadata> emptyMetadatas = new HashSet<KoodistoRyhmaMetadata>();
            KoodistoRyhmaMetadata emptyMetadata = new KoodistoRyhmaMetadata();
            emptyMetadata.setKieli(Kieli.FI);
            emptyMetadata.setNimi("");
            emptyMetadatas.add(emptyMetadata);
            dto.setKoodistoRyhmaMetadatas(emptyMetadatas);
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä with empty metadata accepted.");
        } catch (KoodistoRyhmaNimiEmptyException e) {
        }
        assertNull(group);

    }

    @Test
    public void testUpdateKoodistoRyhmaByAddingMetadata() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-4L);
        assertEquals(1, group.getKoodistoJoukkoMetadatas().size());
        KoodistoRyhmaDto dto = createRyhma(group);
        KoodistoRyhmaMetadata sv = new KoodistoRyhmaMetadata();
        sv.setKieli(Kieli.SV);
        sv.setNimi("SV Name");
        Set<KoodistoRyhmaMetadata> md = new HashSet<KoodistoRyhmaMetadata>();
        md.addAll(group.getKoodistoJoukkoMetadatas());
        md.add(sv);
        dto.setKoodistoRyhmaMetadatas(md);

        KoodistoRyhma response = resource.updateKoodistoRyhma(dto);
        assertNotNull(response);

    }

    @Test
    public void testUpdateKoodistoRyhmaByChangingMetadata() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-6L);
        assertEquals(3, group.getKoodistoJoukkoMetadatas().size());

        for (KoodistoRyhmaMetadata metadata : group.getKoodistoJoukkoMetadatas()) {
            metadata.setNimi("Updated Name");
        }
        KoodistoRyhmaDto dto = createRyhma(group);
        KoodistoRyhma response = resource.updateKoodistoRyhma(dto);
        assertNotNull(response);

        group = resource.getKoodistoRyhmaById(-6L);
        assertEquals(3, group.getKoodistoJoukkoMetadatas().size());
        for (KoodistoRyhmaMetadata metadata : group.getKoodistoJoukkoMetadatas()) {
            assertEquals("Updated Name", metadata.getNimi());
        }
    }

    @Test
    public void testUpdateKoodistoRyhmaByRemovingMetadata() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-6L);
        assertEquals(3, group.getKoodistoJoukkoMetadatas().size());

        boolean first = true;
        ;
        for (KoodistoRyhmaMetadata metadata : group.getKoodistoJoukkoMetadatas()) {
            if (first) {
                first = false;
                metadata.setNimi(""); // First is removed
            } else {
                metadata.setNimi("Updated Name");
            }
        }
        KoodistoRyhmaDto dto = createRyhma(group);
        KoodistoRyhma response = resource.updateKoodistoRyhma(dto);
        assertNotNull(response);

        group = resource.getKoodistoRyhmaById(-6L);
        assertEquals(2, group.getKoodistoJoukkoMetadatas().size());
    }

    @Test
    @Transactional
    public void testInvalidUpdateKoodistoRyhmaShouldFail() {
        KoodistoRyhma koodistoRyhma = resource.getKoodistoRyhmaById(-6L);
        KoodistoRyhmaDto dto = null;

        KoodistoRyhma group = null;
        try {
            group = resource.updateKoodistoRyhma(null);
            fail("Null DTO on update accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));

        try {
            dto = createRyhma(koodistoRyhma);
            dto.setKoodistoRyhmaUri(null);
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä without uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));

        try {
            dto = createRyhma(koodistoRyhma);
            dto.setKoodistoRyhmaUri("");
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä with empty uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));

        try {
            dto = createRyhma(koodistoRyhma);
            dto.setKoodistoRyhmaMetadatas(new HashSet<>());
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä without metadata accepted.");
        } catch (MetadataEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));
        
        try {
            dto = createRyhma(koodistoRyhma);
            Set<KoodistoRyhmaMetadata> emptyMetadatas = new HashSet<KoodistoRyhmaMetadata>();
            KoodistoRyhmaMetadata emptyMetadata = new KoodistoRyhmaMetadata();
            emptyMetadata.setKieli(Kieli.FI);
            emptyMetadata.setNimi("");
            emptyMetadatas.add(emptyMetadata);
            dto.setKoodistoRyhmaMetadatas(emptyMetadatas);
            group = resource.createKoodistoRyhma(dto);
            fail("Koodistoryhmä with empty metadata accepted.");
        } catch (KoodistoRyhmaNimiEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));
    }

    @Test
    @Transactional
    public void testGetKoodistoRyhmaById() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(group);
        assertEquals(5, group.getKoodistos().size());
        assertEquals(1, group.getKoodistoJoukkoMetadatas().size());
        assertEquals("relaatioidenlisaaminen", group.getKoodistoRyhmaUri());
    }

    @Test(expected = KoodistoRyhmaNotFoundException.class)
    public void testGetKoodistoRyhmaByNullId() {
       resource.getKoodistoRyhmaById(null);
    }

    @Test(expected = KoodistoRyhmaNotFoundException.class)
    public void testGetKoodistoRyhmaByInvalidId() {
        resource.getKoodistoRyhmaById(-987123L);
    }

    @Test(expected = KoodistoRyhmaNotFoundException.class)
    public void testGetKoodistoRyhmaByInvalidIdZero() {
        resource.getKoodistoRyhmaById(0L);
    }

    @Test
    public void testDelete() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-3L);
        assertNotNull(group);
        resource.delete(-3L);
        KoodistoRyhma groupAfter = null;
        try {
            groupAfter = resource.getKoodistoRyhmaById(-3L);
            fail("Did not throw exception.");
        } catch (KoodistoRyhmaNotFoundException e) {
        }
        assertNull(groupAfter);
    }

    @Test
    public void testNonEmptyDeleteFails() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(group);
        try {
            resource.delete(-1L);
            fail("Did not throw exception.");
        } catch (KoodistoRyhmaNotEmptyException e) {
        }
        KoodistoRyhma groupAfter = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(groupAfter);
    }

    // UTILITIES
    // /////////
    private KoodistoRyhmaDto createRyhma(String koodistoRyhmaUri) {
        KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
        dto.setKoodistoRyhmaUri(koodistoRyhmaUri);

        Set<KoodistoRyhmaMetadata> metadataSet = new HashSet<>();
        KoodistoRyhmaMetadata fi = new KoodistoRyhmaMetadata();
        fi.setKieli(Kieli.FI);
        fi.setNimi(koodistoRyhmaUri);
        metadataSet.add(fi);
        dto.setKoodistoRyhmaMetadatas(metadataSet);

        Set<Koodisto> emptyKoodistoSet = new HashSet<>();
        dto.setKoodistos(emptyKoodistoSet);
        return dto;
    }

    private KoodistoRyhmaDto createRyhma(KoodistoRyhma g) {
        KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
        dto.setId(g.getId());
        dto.setKoodistoRyhmaMetadatas(g.getKoodistoJoukkoMetadatas());
        dto.setKoodistoRyhmaUri(g.getKoodistoRyhmaUri());
        dto.setKoodistos(g.getKoodistos());
        return dto;
    }

    private void assertEqualRyhmas(KoodistoRyhma kr1, KoodistoRyhma kr2) {
        assertEquals(kr1.getId(), kr2.getId());
        assertEquals(kr1.getKoodistoRyhmaUri(), kr2.getKoodistoRyhmaUri());
        assertEquals(kr1.getKoodistoJoukkoMetadatas().size(), kr2.getKoodistoJoukkoMetadatas().size());
        assertEquals(kr1.getKoodistos().size(), kr2.getKoodistos().size());
        assertEquals(kr1.getKoodistoJoukkoMetadatas(), kr2.getKoodistoJoukkoMetadatas());
        assertEquals(kr1.getKoodistos(), kr2.getKoodistos());
    }
}
