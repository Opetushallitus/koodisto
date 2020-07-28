package fi.vm.sade.koodisto.service.business.impl;

import java.util.HashSet;
import java.util.Set;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.service.business.DownloadBusinessService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@DataJpaTest
@ActiveProfiles("test")
@DatabaseSetup("classpath:test-data.xml")
@DatabaseSetup("classpath:test-data-codes-rest.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoRyhmaBusinessServiceIT {

    @MockBean
    private DownloadBusinessService downloadBusinessService;

    @MockBean
    private KoodiBusinessService koodiBusinessService;

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

    @Test(expected = KoodistoRyhmaUriEmptyException.class)
    public void creatingNullKoodistoRyhmaFails() {
        resource.createKoodistoRyhma(null);
    }

    @Test(expected = KoodistoRyhmaUriEmptyException.class)
    public void creatingKoodistoRyhmaWithNullUriFails() {
        resource.createKoodistoRyhma(createRyhma((String) null));
    }

    @Test(expected = KoodistoRyhmaUriEmptyException.class)
    public void creatingKoodistoRyhmaWithBlankUriFails() {
        resource.createKoodistoRyhma(createRyhma(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createKoodistoRyhmaWithExistingIdFails() {
        KoodistoRyhma existing = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(existing);
        KoodistoRyhmaDto dto = createRyhma(existing);
        resource.createKoodistoRyhma(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createKoodistoRyhmaWithExistingUriFails() {
        KoodistoRyhma existing = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(existing);
        KoodistoRyhmaDto dto = createRyhma(existing);
        dto.setId(null);
        resource.createKoodistoRyhma(dto);
    }

    @Test(expected = MetadataEmptyException.class)
    public void createKoodistoRyhmaWithEmptyMetadataFails() {
        KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
        dto.setKoodistoRyhmaMetadatas(new HashSet<>());
        resource.createKoodistoRyhma(dto);
    }

    @Test(expected = KoodistoRyhmaNimiEmptyException.class)
    public void createKoodistoRyhmaWithEmptyNimiFails() {
        KoodistoRyhmaDto dto = createRyhma("thisshouldfail");
        Set<KoodistoRyhmaMetadata> emptyMetadatas = new HashSet<>();
        KoodistoRyhmaMetadata emptyMetadata = new KoodistoRyhmaMetadata();
        emptyMetadata.setKieli(Kieli.FI);
        emptyMetadata.setNimi("");
        emptyMetadatas.add(emptyMetadata);
        dto.setKoodistoRyhmaMetadatas(emptyMetadatas);
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
        Set<KoodistoRyhmaMetadata> md = new HashSet<>(group.getKoodistoJoukkoMetadatas());
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

        Set<KoodistoRyhmaMetadata> updated = new HashSet<>();
        boolean first = true;
        for (KoodistoRyhmaMetadata metadata : group.getKoodistoJoukkoMetadatas()) {
            if (first) {
                first = false;
            } else {
                updated.add(metadata);
            }
        }
        group.setKoodistoRyhmaMetadatas(updated);
        KoodistoRyhmaDto dto = createRyhma(group);
        KoodistoRyhma response = resource.updateKoodistoRyhma(dto);
        assertNotNull(response);

        group = resource.getKoodistoRyhmaById(-6L);
        assertEquals(2, group.getKoodistoJoukkoMetadatas().size());
    }

    @Test
    public void testInvalidUpdateKoodistoRyhmaShouldFail() {
        KoodistoRyhma koodistoRyhma = resource.getKoodistoRyhmaById(-6L);
        KoodistoRyhmaDto dto;

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
            group = resource.updateKoodistoRyhma(dto);
            fail("Koodistoryhmä without uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));

        try {
            dto = createRyhma(koodistoRyhma);
            dto.setKoodistoRyhmaUri("");
            group = resource.updateKoodistoRyhma(dto);
            fail("Koodistoryhmä with empty uri accepted.");
        } catch (KoodistoRyhmaUriEmptyException e) {
        }
        assertNull(group);
        assertEqualRyhmas(koodistoRyhma, resource.getKoodistoRyhmaById(-6L));
    }

    @Test
    public void testGetKoodistoRyhmaById() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(-1L);
        assertNotNull(group);
        assertEquals(5, group.getKoodistos().size());
        assertEquals(1, group.getKoodistoJoukkoMetadatas().size());
        assertEquals("relaatioidenlisaaminen", group.getKoodistoRyhmaUri());
    }

    @Test(expected = KoodistoRyhmaNotFoundException.class)
    public void testGetKoodistoRyhmaByInvalidId() {
        KoodistoRyhma group = resource.getKoodistoRyhmaById(null);
        assertNull(group);

        group = resource.getKoodistoRyhmaById(-987123L);
        assertNull(group);

        group = resource.getKoodistoRyhmaById(0L);
        assertNull(group);
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
