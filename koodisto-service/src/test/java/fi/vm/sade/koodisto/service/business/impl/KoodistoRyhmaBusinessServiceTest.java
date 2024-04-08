package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
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
import java.util.stream.Collectors;

import static java.util.Set.of;
import static org.junit.Assert.*;

@Sql("/truncate_tables.sql")
@Sql("/test-data-codes-rest.sql")
@SpringBootTest
@RunWith(SpringRunner.class)
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

    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateInvalidKoodistoRyhmaFails() {
        KoodistoRyhmaDto dto = createRyhma("relaatioidenlisaaminen");
        resource.createKoodistoRyhma(dto);
    }

    @Test
    public void testUpdateKoodistoRyhmaByAddingMetadata() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-4L);
        assertEquals(1, group.getKoodistoJoukkoMetadatas().size());
        KoodistoRyhmaDto dto = createRyhma(group);
        KoodistoRyhmaMetadata sv = new KoodistoRyhmaMetadata();
        sv.setKieli(Kieli.SV);
        sv.setNimi("SV Name");
        sv.setKoodistoRyhma(group);
        Set<KoodistoRyhmaMetadata> md = new HashSet<KoodistoRyhmaMetadata>();
        md.addAll(group.getKoodistoJoukkoMetadatas());
        md.add(sv);
        dto.setKoodistoRyhmaMetadatas(md.stream().map(this::convert).collect(Collectors.toSet()));

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
        dto.setKoodistoRyhmaMetadatas(of(
                KoodistoRyhmaMetadataDto.builder()
                        .kieli(Kieli.FI)
                        .nimi(koodistoRyhmaUri)
                        .build()));
        dto.setKoodistos(of());
        return dto;
    }

    private KoodistoRyhmaDto createRyhma(KoodistoRyhma g) {
        KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
        dto.setId(g.getId());
        dto.setKoodistoRyhmaMetadatas(g.getKoodistoJoukkoMetadatas().stream()
                .map(this::convert)
                .collect(Collectors.toSet()));
        dto.setKoodistoRyhmaUri(g.getKoodistoRyhmaUri());
        dto.setKoodistos(g.getKoodistos());
        return dto;
    }

    private KoodistoRyhmaMetadataDto convert(final KoodistoRyhmaMetadata metadata) {
        return KoodistoRyhmaMetadataDto.builder()
                .id(metadata.getId())
                .uri(metadata.getKoodistoRyhma().getKoodistoRyhmaUri())
                .nimi(metadata.getNimi())
                .kieli(metadata.getKieli())
                .build();
    }
}
