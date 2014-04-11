package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.*;
import fi.vm.sade.koodisto.service.business.exception.*;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.*;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class KoodiAdminServiceTest {

    @Autowired
    private KoodiAdminService koodiAdminService;

    @Autowired
    private KoodistoAdminService koodistoAdminService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private KoodiService koodiService;

    private KoodistoType getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder
                .latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchCriteria);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing.");
        }

        return koodistos.get(0);
    }

    private KoodiType getKoodiByUri(String koodiUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);

        if (koodis.size() != 1) {
            throw new RuntimeException("Failing.");
        }

        return koodis.get(0);
    }

    private KoodiType getKoodiByUriAndVersio(String koodiUri, Integer koodiVersio) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(koodiUri,
                koodiVersio);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);

        if (koodis.size() != 1) {
            throw new RuntimeException("Failing.");
        }

        return koodis.get(0);
    }

    @Test
    public void testCreate() {
        String koodistoUri = "http://testi.fi";

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
        assertEquals(1, koodisto.getVersio());

        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType("arvo", new Date(), null, "nimi");
        koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
        koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, koodisto.getTila());
        assertEquals(2, koodisto.getVersio());

    }

    @Test(expected = GenericFault.class)
    @Ignore // Ei enää validi.
    public void testCreateKoodiWithNonUniqueNimi() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String koodiNimi = "haapavesi";

        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType("arvo", new Date(), new Date(),
                koodiNimi);

        try {
            koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);

        } catch (GenericFault e) {
            assertEquals(KoodiNimiNotUniqueException.ERROR_KEY, e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test
    public void testUpdate() {

        String koodistoUri = "http://testi.fi";

        List<KoodiType> koodis = listKoodisByKoodisto(koodistoUri, 1);
        assertEquals(0, koodis.size());

        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType("arvo", new Date(), null, "nimi");
        KoodiType dto = koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        Integer koodistoVersion = koodisto.getVersio();
        assertEquals(2, koodistoVersion.intValue());

        koodis = listKoodisByKoodisto(koodistoUri, 2);
        assertEquals(1, koodis.size());
        assertEquals("arvo", koodis.get(0).getKoodiArvo());

        UpdateKoodiDataType updateKoodiData = new UpdateKoodiDataType();
        DataUtils.copyFields(dto, updateKoodiData);
        updateKoodiData.setTila(UpdateKoodiTilaType.LUONNOS);
        koodiAdminService.updateKoodi(updateKoodiData);
        dto = getKoodiByUri(dto.getKoodiUri());
        assertEquals(TilaType.LUONNOS, dto.getTila());

        String koodistoUri2 = "http://www.kunnat.fi/kunta";
        String koodiUri = "3";

        koodisto = getKoodistoByUri(koodistoUri2);
        assertEquals(1, koodisto.getVersio());
        assertEquals(100, listKoodisByKoodisto(koodistoUri2, 1).size());

        KoodiType kunta = getKoodiByUri(koodiUri);
        assertEquals(1, kunta.getVersio());
        DataUtils.copyFields(kunta, updateKoodiData);

        for (KoodiMetadataType foo : updateKoodiData.getMetadata()) {
            foo.setNimi("fadsfasdf");
        }

        kunta = koodiAdminService.updateKoodi(updateKoodiData);
        assertEquals(2, kunta.getVersio());

        koodisto = getKoodistoByUri(koodistoUri2);
        assertEquals(2, koodisto.getVersio());
        assertEquals(100, listKoodisByKoodisto(koodistoUri2, 2).size());
    }

    @Test(expected = GenericFault.class)
    public void testUpdateWithInsufficientMetadataFields() {
        final String koodiUri = "435";
        KoodiType koodiToUpdate = getKoodiByUri(koodiUri);

        assertEquals(3, koodiToUpdate.getMetadata().size());

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiToUpdate, updateData);

        KoodiMetadataType enMeta = KoodistoHelper.getKoodiMetadataForLanguage(updateData.getMetadata(), KieliType.EN);
        enMeta.setNimi("");
        boolean caughtOne = false;

        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (GenericFault e) {
            caughtOne = true;
            assertEquals(KoodiNimiEmptyException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
        } catch (Exception e) {
            fail();
        }

        assertTrue(caughtOne);

        enMeta.setNimi("non empty");
        enMeta.setKuvaus("");
        caughtOne = false;

        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (GenericFault e) {
            caughtOne = true;
            assertEquals(KoodiKuvausEmptyException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
        } catch (Exception e) {
            fail();
        }

        assertTrue(caughtOne);

        enMeta.setKuvaus("non empty");
        enMeta.setLyhytNimi("");

        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (GenericFault e) {
            assertEquals(KoodiLyhytNimiEmptyException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test(expected = GenericFault.class)
    @Ignore // Ei enää validi.
    public void testUpdateWithNonUniqueNimi() {
        final String koodiToUpdateUri = "29";

        KoodiType koodiToUpdate = getKoodiByUri(koodiToUpdateUri);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiToUpdate, updateData);

        // Let's first try to update the koodi without changing any fields. This
        // should not cause an exception.
        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (Exception e) {
            fail();
        }

        // Now, let's change the nimi into something non-unique
        final String koodiUri = "3";
        KoodiType kunta = getKoodiByUri(koodiUri);
        String nimi = kunta.getMetadata().get(0).getNimi().toLowerCase();

        for (KoodiMetadataType m : updateData.getMetadata()) {
            m.setNimi(nimi);
        }

        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (GenericFault e) {
            assertEquals(KoodiNimiNotUniqueException.ERROR_KEY, e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test
    public void testUpdateWithoutSendingMetadataForAllLanguages() {
        final String koodiUri = "435";
        KoodiType koodiToUpdate = getKoodiByUri(koodiUri);

        assertEquals(3, koodiToUpdate.getMetadata().size());

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiToUpdate, updateData);

        KoodiMetadataType enMeta = KoodistoHelper.getKoodiMetadataForLanguage(updateData.getMetadata(), KieliType.EN);
        updateData.getMetadata().remove(enMeta);
        assertEquals(2, updateData.getMetadata().size());
        updateData.setKoodiArvo("2");
        koodiAdminService.updateKoodi(updateData);

        KoodiType updated = getKoodiByUri(koodiUri);
        assertEquals(2, updated.getMetadata().size());
        for (KoodiMetadataType m : updated.getMetadata()) {
            assertFalse(KieliType.EN.equals(m.getKieli()));
        }
    }

    /**
     * Updates an accepted koodi and checks that the old version of the koodi is
     * not included in the koodisto which is in draft state
     */
    @Test
    public void testUpdateAcceptedKoodi() {
        final String koodistoUri = "http://ekaversioluonnostilassa";
        final int koodistoVersio = 1;

        final String koodiUri = "437";
        final int koodiVersio = 1;

        assertEquals(1, listKoodisByKoodisto(koodistoUri, koodistoVersio).size());

        KoodiType koodi = getKoodiByUri(koodiUri);
        assertEquals(koodiVersio, koodi.getVersio());

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        // Let's change the name. This should create a new version of the koodi
        for (KoodiMetadataType km : updateData.getMetadata()) {
            km.setNimi("modified");
        }

        koodiAdminService.updateKoodi(updateData);
        koodi = getKoodiByUri(koodiUri);
        assertEquals(koodiVersio + 1, koodi.getVersio());

        List<KoodiType> koodis = listKoodisByKoodisto(koodistoUri, koodistoVersio);
        assertEquals(1, koodis.size());
        assertEquals(koodi, koodis.get(0));
    }

    private List<KoodiType> listKoodisByKoodisto(String koodistoUri, Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);
        return koodiService.searchKoodisByKoodisto(searchCriteria);
    }

    @Test
    public void testAnotherUpdate() {
        final String koodiUri = "411";
        final int koodiVersioBefore = 1;
        final int koodiVersioAfter = 2;

        final String koodistoUri = "http://testikoodisto-2.fi";
        final int koodistoVersioBefore = 1;
        final int koodistoVersioAfter = 2;

        KoodiType koodiDTO = getKoodiByUri(koodiUri);
        assertEquals(koodiVersioBefore, koodiDTO.getVersio());
        assertEquals(TilaType.HYVAKSYTTY, koodiDTO.getTila());

        KoodistoType koodistoDTO1 = getKoodistoByUri(koodistoUri);
        assertEquals(koodistoVersioBefore, koodistoDTO1.getVersio());
        assertEquals(TilaType.HYVAKSYTTY, koodistoDTO1.getTila());

        List<KoodiType> koodis = listKoodisByKoodisto(koodistoUri, koodistoVersioBefore);

        boolean found = false;
        for (KoodiType k : koodis) {
            if (koodiUri.equals(k.getKoodiUri()) && koodiVersioBefore == k.getVersio()) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        UpdateKoodiDataType updateKoodiData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiDTO, updateKoodiData);

        final String newName = "modified";
        for (KoodiMetadataType m : updateKoodiData.getMetadata()) {
            assertFalse(newName.equals(m.getNimi()));
            m.setNimi(newName);
        }

        koodiAdminService.updateKoodi(updateKoodiData);

        koodiDTO = getKoodiByUri(koodiUri);
        assertEquals(koodiVersioAfter, koodiDTO.getVersio());
        assertEquals(TilaType.LUONNOS, koodiDTO.getTila());

        koodistoDTO1 = getKoodistoByUri(koodistoUri);
        assertEquals(koodistoVersioAfter, koodistoDTO1.getVersio());
        assertEquals(TilaType.LUONNOS, koodistoDTO1.getTila());

        koodis = listKoodisByKoodisto(koodistoUri, koodistoVersioAfter);
        found = false;
        for (KoodiType k : koodis) {
            if (koodiUri.equals(k.getKoodiUri())) {
                // Check that the new version of koodi is found
                if (koodiVersioAfter == k.getVersio()) {
                    found = true;
                }

                // Check that the old koodi is not found in the koodisto
                assertFalse(koodiVersioBefore == k.getVersio());
            }
        }
        assertTrue(found);

        for (KoodiMetadataType m : koodiDTO.getMetadata()) {
            assertTrue(newName.equals(m.getNimi()));
        }
    }

    @Test(expected=fi.vm.sade.koodisto.service.GenericFault.class)
    public void testUpdateAcceptedKoodisOldVersio() {
        final String koodiUri = "436";
        final int versio = 2;

        final int latestVersio = 12;
        final Integer numberOfMetadatas = 3;

        KoodiType koodiDTO = getKoodiByUriAndVersio(koodiUri, versio);
        assertEquals(koodiUri, koodiDTO.getKoodiUri());
        assertEquals(versio, koodiDTO.getVersio());
        assertEquals(numberOfMetadatas, new Integer(koodiDTO.getMetadata().size()));

        UpdateKoodiDataType updateKoodiData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiDTO, updateKoodiData);

        final String newName = "modified";
        // Do some modifications
        for (KoodiMetadataType meta : updateKoodiData.getMetadata()) {
            assertFalse(newName.equals(meta.getNimi()));
            meta.setNimi(newName);
        }

        koodiAdminService.updateKoodi(updateKoodiData);

    }

    @Test(expected=fi.vm.sade.koodisto.service.GenericFault.class)
    public void testUpdateDraftKoodisOldVersio() {
        final String koodiUri = "435";
        final int versio = 2;

        final int latestVersio = 11;

        KoodiType koodiDTO = getKoodiByUriAndVersio(koodiUri, versio);
        assertEquals(koodiUri, koodiDTO.getKoodiUri());
        assertEquals(versio, koodiDTO.getVersio());

        UpdateKoodiDataType updateKoodiData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodiDTO, updateKoodiData);

        final String newName = "modified";
        // Do some modifications
        for (KoodiMetadataType meta : updateKoodiData.getMetadata()) {
            assertFalse(newName.equals(meta.getNimi()));
            meta.setNimi(newName);
        }

        koodiAdminService.updateKoodi(updateKoodiData);

    }

    @Test(expected = RuntimeException.class)
    public void testDeletePassiveVersion() {
        final String koodiUri = "410";
        final Integer koodiVersio = 2;

        KoodiType koodi = getKoodiByUriAndVersio(koodiUri, koodiVersio);
        assertNotNull(koodi);
        assertEquals(TilaType.PASSIIVINEN, koodi.getTila());

        koodiAdminService.deleteKoodiVersion(koodiUri, koodiVersio);

        getKoodiByUriAndVersio(koodiUri, koodiVersio);
    }

    @Test(expected = GenericFault.class)
    public void testDeleteNonPassiveVersion() {
        final String koodiUri = "410";
        final Integer koodiVersio = 1;

        KoodiType koodi = getKoodiByUriAndVersio(koodiUri, koodiVersio);
        assertNotNull(koodi);
        assertFalse(TilaType.PASSIIVINEN.equals(koodi.getTila()));

        try {
            koodiAdminService.deleteKoodiVersion(koodiUri, koodiVersio);
        } catch (GenericFault e) {
            assertEquals(KoodiVersioNotPassiivinenException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    private KoodiUriAndVersioType createKoodiUriAndVersioType(KoodiType koodi) {
        KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodi.getKoodiUri());
        k.setVersio(koodi.getVersio());
        return k;
    }

    private KoodiUriAndVersioType createKoodiUriAndVersioType(String koodiUri, int koodiVersio) {
        KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodiUri);
        k.setVersio(koodiVersio);

        return k;
    }

    @Test
    public void testChangingKoodiNimiCreatesNewVersio() {
        final String koodiUri = "411";
        KoodiType koodi = getKoodiByUri(koodiUri);
        assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());

        int versioBefore = koodi.getVersio();
        KoodiMetadataType fiMeta = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.FI);

        final String uusiNimi = "uusinimi";
        assertTrue(!fiMeta.getNimi().equals(uusiNimi));
        fiMeta.setNimi(uusiNimi);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);

        KoodiType updatedKoodi = getKoodiByUri(koodiUri);
        assertEquals(versioBefore + 1, updatedKoodi.getVersio());
    }

    @Test
    public void testChangingKoodiLyhytNimiCreatesNewVersio() {
        final String koodiUri = "411";
        KoodiType koodi = getKoodiByUri(koodiUri);
        assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());

        int versioBefore = koodi.getVersio();
        KoodiMetadataType fiMeta = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.FI);

        final String uusiNimi = "uusinimi";
        assertTrue(!fiMeta.getLyhytNimi().equals(uusiNimi));
        fiMeta.setLyhytNimi(uusiNimi);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);

        KoodiType updatedKoodi = getKoodiByUri(koodiUri);
        assertEquals(versioBefore + 1, updatedKoodi.getVersio());
    }

    @Test
    public void testChangingKoodiArvoCreatesNewVersio() {
        final String koodiUri = "411";
        KoodiType koodi = getKoodiByUri(koodiUri);
        assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());

        int versioBefore = koodi.getVersio();

        final String uusiArvo = "uusiArvo";
        assertTrue(!koodi.getKoodiArvo().equals(uusiArvo));
        koodi.setKoodiArvo(uusiArvo);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);

        KoodiType updatedKoodi = getKoodiByUri(koodiUri);
        assertEquals(versioBefore + 1, updatedKoodi.getVersio());
    }

    @Test
    public void testDeletingKoodiCreatesNewVersioOfKoodisto() {
        final String koodistoUri = "http://koodisto2";
        final int versioBefore = 1;

        {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(versioBefore, koodisto.getVersio());

            final String koodiUri = "444";
            final int koodiVersio = 1;
            List<KoodiType> koodis = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(
                    koodiUri, koodiVersio));
            assertEquals(1, koodis.size());

            KoodiType koodiType = koodis.get(0);

            // Check that the koodi we are about delete is indeed attached to
            // the koodisto versio
            assertEquals(koodistoUri, koodiType.getKoodisto().getKoodistoUri());
            assertTrue(koodiType.getKoodisto().getKoodistoVersio().contains(versioBefore));

            koodiAdminService.deleteKoodiVersion(koodiUri, koodiVersio);
        }

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(versioBefore + 1, koodisto.getVersio());
    }

    @Test
    public void testAddingNewKoodiCreateNewVersioOfKoodisto() {
        final String koodistoUri = "http://koodisto2";
        final int versioBefore = 1;

        {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(versioBefore, koodisto.getVersio());
        }

        CreateKoodiDataType createData = DataUtils.createCreateKoodiDataType("koodiarvo00", new Date(), null,
                "uuden koodin nimi");

        koodiAdminService.createKoodi(koodistoUri, createData);

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(versioBefore + 1, koodisto.getVersio());
    }

    @Test
    public void testNewKoodiIsInDraftStateAfterCreation() {
        final String koodistoUri = "http://koodisto2";

        CreateKoodiDataType createData = DataUtils.createCreateKoodiDataType("koodiarvo00", new Date(), null,
                "uuden koodin nimi");

        String koodiUri = koodiAdminService.createKoodi(koodistoUri, createData).getKoodiUri();
        KoodiType saved = getKoodiByUri(koodiUri);
        assertEquals(TilaType.LUONNOS, saved.getTila());
    }

    @Test
    public void testUpdatedKoodiIsInDraftState() {
        final String koodiUri = "443";
        final int versioBefore = 1;

        KoodiType koodi = getKoodiByUri(koodiUri);

        {
            assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());
            assertEquals(versioBefore, koodi.getVersio());
        }

        UpdateKoodiDataType updateData = DataUtils.createUpdateKoodiDataType(koodiUri, "1234", null,
                new Date(), null, "uusikoodinimi", koodi.getVersio(), koodi.getLockingVersion());
        koodiAdminService.updateKoodi(updateData);
        KoodiType updated = getKoodiByUri(koodiUri);
        assertEquals(TilaType.LUONNOS, updated.getTila());
        assertEquals(versioBefore + 1, updated.getVersio());

    }

    private List<KoodiType> getKoodisByKoodisto(String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoUri);
        return koodiService.searchKoodisByKoodisto(searchData);
    }

    @Test
    public void testUpdatingKoodiCreatesNewVersioOfKoodistoAndAllKoodisInKoodisto() {
        final String koodistoUri = "http://koodisto3";
        final String koodiUri = "445";
        final int versioBefore = 1;
        final TilaType tilaBefore = TilaType.HYVAKSYTTY;

        KoodiType koodi = getKoodiByUri(koodiUri);

        {
            assertEquals(tilaBefore, koodi.getTila());
            assertEquals(versioBefore, koodi.getVersio());

            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(tilaBefore, koodisto.getTila());
            assertEquals(versioBefore, koodisto.getVersio());

            List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
            assertEquals(3, koodis.size());

            for (KoodiType k : koodis) {
                assertEquals(versioBefore, k.getVersio());
                assertEquals(tilaBefore, k.getTila());
            }
        }

        UpdateKoodiDataType updateData = DataUtils.createUpdateKoodiDataType(koodiUri, "arvo", null,
                new Date(), null, "uusikoodinimi", koodi.getVersio(), koodi.getLockingVersion());

        koodiAdminService.updateKoodi(updateData);

        final TilaType tilaAfter = TilaType.LUONNOS;
        final int versioAfter = versioBefore + 1;

        koodi = getKoodiByUri(koodiUri);
        assertEquals(tilaAfter, koodi.getTila());
        assertEquals(versioAfter, koodi.getVersio());

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(tilaAfter, koodisto.getTila());
        assertEquals(versioAfter, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(3, koodis.size());

        for (KoodiType k : koodis) {
            assertEquals(versioAfter, k.getVersio());
            assertEquals(tilaAfter, k.getTila());
        }
    }

    /**
     * If a koodisto is in draft state, no new version should be created for it
     */
    @Test
    public void testUpdatingKoodiDoesNotCreateNewVersioOfDraftKoodisto() {
        final String koodistoUri = "http://koodisto4";
        final String koodiUri = "448";
        final int versioBefore = 1;
        final TilaType tilaBefore = TilaType.HYVAKSYTTY;

        KoodiType koodi = getKoodiByUri(koodiUri);

        {
            assertEquals(tilaBefore, koodi.getTila());
            assertEquals(versioBefore, koodi.getVersio());

            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(TilaType.LUONNOS, koodisto.getTila());
            assertEquals(versioBefore, koodisto.getVersio());

            List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
            assertEquals(3, koodis.size());

            Collections.sort(koodis, new Comparator<KoodiType>() {
                @Override
                public int compare(KoodiType o1, KoodiType o2) {
                    return o1.getKoodiUri().compareTo(o2.getKoodiUri());
                }
            });

            assertEquals("448", koodis.get(0).getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, koodis.get(0).getTila());
            assertEquals(versioBefore, koodis.get(0).getVersio());

            assertEquals("449", koodis.get(1).getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, koodis.get(1).getTila());
            assertEquals(versioBefore, koodis.get(1).getVersio());

            assertEquals("450", koodis.get(2).getKoodiUri());
            assertEquals(TilaType.LUONNOS, koodis.get(2).getTila());
            assertEquals(versioBefore, koodis.get(2).getVersio());
        }

        UpdateKoodiDataType updateData = DataUtils.createUpdateKoodiDataType(koodiUri, "arvo", null,
                new Date(), null, "uusikoodinimi", koodi.getVersio(), koodi.getLockingVersion());

        koodiAdminService.updateKoodi(updateData);

        final int versioAfter = versioBefore + 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, koodisto.getTila());
        assertEquals(versioBefore, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(3, koodis.size());

        Collections.sort(koodis, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiUri().compareTo(o2.getKoodiUri());
            }
        });

        assertEquals("448", koodis.get(0).getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodis.get(0).getTila());
        assertEquals(versioAfter, koodis.get(0).getVersio());

        assertEquals("449", koodis.get(1).getKoodiUri());
        assertEquals(TilaType.HYVAKSYTTY, koodis.get(1).getTila());
        assertEquals(versioBefore, koodis.get(1).getVersio());

        assertEquals("450", koodis.get(2).getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodis.get(2).getTila());
        assertEquals(versioBefore, koodis.get(2).getVersio());
    }

    @Test
    public void testUpdatingKoodiMetadataSetsUpdatedAtDate() {
        final String koodiUri = "453";
        final int versio = 1;
        KoodiType koodi = getKoodiByUri(koodiUri);
        Date now = new Date();
        {
            assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());
            assertEquals(versio, koodi.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodi.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }


        KoodiMetadataType metaFi = KoodistoHelper.getKoodiMetadataForLanguage(koodi, KieliType.FI);
        final String newSisaltaaKoodiston = "uusi arvo";
        assertNull(metaFi.getSisaltaaKoodiston());
        metaFi.setSisaltaaKoodiston(newSisaltaaKoodiston);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);
        KoodiType updated = getKoodiByUri(koodiUri);
        assertEquals(TilaType.HYVAKSYTTY, updated.getTila());
        assertEquals(versio, updated.getVersio());
        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());

        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testUpdatingDraftKoodiArvoSetsUpdatedAtDate() {
        final String koodiUri = "451";
        final int versio = 1;
        KoodiType koodi = getKoodiByUri(koodiUri);
        Date now = new Date();
        {
            assertEquals(TilaType.LUONNOS, koodi.getTila());
            assertEquals(versio, koodi.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodi.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }


        final String newArvo = "uusi arvo";
        assertFalse(koodi.getKoodiArvo().equals(newArvo));
        koodi.setKoodiArvo(newArvo);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);
        KoodiType updated = getKoodiByUri(koodiUri);
        assertEquals(TilaType.LUONNOS, updated.getTila());
        assertEquals(versio, updated.getVersio());

        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());

        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testUpdatingAcceptedKoodiArvoSetsUpdatedAtDate() {
        final String koodiUri = "453";
        final int versio = 1;
        KoodiType koodi = getKoodiByUri(koodiUri);
        Date now = new Date();
        {
            assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());
            assertEquals(versio, koodi.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodi.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }


        final String newArvo = "uusi arvo";
        assertFalse(koodi.getKoodiArvo().equals(newArvo));
        koodi.setKoodiArvo(newArvo);

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);

        koodiAdminService.updateKoodi(updateData);
        KoodiType updated = getKoodiByUri(koodiUri);
        assertEquals(TilaType.LUONNOS, updated.getTila());
        assertEquals(versio + 1, updated.getVersio());

        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());

        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testMassCreateInDraftKoodisto() {
        final String koodistoUri = "http://koodisto9";
        final String koodiUri1 = "456";
        final String koodiUri2 = "457";

        UpdateKoodiDataType updateData1 = new UpdateKoodiDataType();
        UpdateKoodiDataType updateData2 = new UpdateKoodiDataType();
        UpdateKoodiDataType createData = null;

        final String createdArvo = "003";
        final String newName1 = "newName1";
        final String newName2 = "newName2";
        final String createdNimi = "uuden luodun nimi";
        {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(TilaType.LUONNOS, koodisto.getTila());
            assertEquals(1, koodisto.getVersio());
            assertEquals(2, getKoodisByKoodisto(koodistoUri).size());

            KoodiType koodi1 = getKoodiByUri(koodiUri1);
            assertEquals(TilaType.HYVAKSYTTY, koodi1.getTila());
            assertEquals(1, koodi1.getVersio());

            KoodiType koodi2 = getKoodiByUri(koodiUri2);
            assertEquals(TilaType.LUONNOS, koodi2.getTila());
            assertEquals(1, koodi2.getVersio());

            KoodiMetadataType fimeta1 = KoodistoHelper.getKoodiMetadataForLanguage(koodi1, KieliType.FI);
            assertFalse(fimeta1.getNimi().equals(newName1));
            fimeta1.setNimi(newName1);

            KoodiMetadataType fimeta2 = KoodistoHelper.getKoodiMetadataForLanguage(koodi2, KieliType.FI);
            assertFalse(fimeta2.getNimi().equals(newName2));
            fimeta2.setNimi(newName2);

            DataUtils.copyFields(koodi1, updateData1);
            DataUtils.copyFields(koodi2, updateData2);

            createData = DataUtils.createUpdateKoodiDataType("", createdArvo, null, new Date(), null,
                    createdNimi, 0, 0);
        }

        List<UpdateKoodiDataType> updateDatas = Arrays.asList(new UpdateKoodiDataType[]{updateData1, updateData2,
                createData});

        koodiAdminService.massCreate(koodistoUri, updateDatas);

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, koodisto.getTila());
        assertEquals(1, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(2, koodis.size());

        Collections.sort(koodis, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiArvo().compareTo(o2.getKoodiArvo());
            }
        });

        KoodiType koodi1 = koodis.get(0);
        KoodiType koodi2 = koodis.get(1);

        assertEquals(koodiUri1, koodi1.getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodi1.getTila());
        assertEquals(2, koodi1.getVersio());
        assertEquals(newName1, KoodistoHelper.getKoodiMetadataForLanguage(koodi1, KieliType.FI).getNimi());

        assertEquals(koodiUri2, koodi2.getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodi2.getTila());
        assertEquals(1, koodi2.getVersio());
        assertEquals(newName2, KoodistoHelper.getKoodiMetadataForLanguage(koodi2, KieliType.FI).getNimi());

    }

    @Test
    public void testMassCreateKoodiNotInKoodisto() {
        final String koodistoUri = "http://koodisto9";
        final String koodiUri = "3";

        KoodiType koodi = getKoodiByUri(koodiUri);
        assertFalse(koodistoUri.equals(koodi.getKoodisto().getKoodistoUri()));

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);
        try {
            koodiAdminService.massCreate(koodistoUri, Arrays.asList(new UpdateKoodiDataType[]{updateData}));
        } catch (GenericFault e) {
            assertEquals(KoodiNotInKoodistoException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test
    public void testMassCreateInAcceptedKoodisto() {
        final String koodistoUri = "http://koodisto10";
        final String koodiUri1 = "458";
        final String koodiUri2 = "459";

        UpdateKoodiDataType updateData1 = new UpdateKoodiDataType();
        UpdateKoodiDataType updateData2 = new UpdateKoodiDataType();
        UpdateKoodiDataType createData = null;

        final String createdArvo = "003";
        final String newName1 = "newName1";
        final String newName2 = "newName2";
        final String createdNimi = "uuden luodun nimi";
        {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(1, koodisto.getVersio());
            assertEquals(2, getKoodisByKoodisto(koodistoUri).size());

            KoodiType koodi1 = getKoodiByUri(koodiUri1);
            assertEquals(TilaType.HYVAKSYTTY, koodi1.getTila());
            assertEquals(1, koodi1.getVersio());

            KoodiType koodi2 = getKoodiByUri(koodiUri2);
            assertEquals(TilaType.LUONNOS, koodi2.getTila());
            assertEquals(1, koodi2.getVersio());

            KoodiMetadataType fimeta1 = KoodistoHelper.getKoodiMetadataForLanguage(koodi1, KieliType.FI);
            assertFalse(fimeta1.getNimi().equals(newName1));
            fimeta1.setNimi(newName1);

            KoodiMetadataType fimeta2 = KoodistoHelper.getKoodiMetadataForLanguage(koodi2, KieliType.FI);
            assertFalse(fimeta2.getNimi().equals(newName2));
            fimeta2.setNimi(newName2);

            DataUtils.copyFields(koodi1, updateData1);
            DataUtils.copyFields(koodi2, updateData2);

            createData = DataUtils.createUpdateKoodiDataType("", createdArvo, null, new Date(), null,
                    createdNimi, 0, 0);
        }

        List<UpdateKoodiDataType> updateDatas = Arrays.asList(new UpdateKoodiDataType[]{updateData1, updateData2,
                createData});

        koodiAdminService.massCreate(koodistoUri, updateDatas);

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, koodisto.getTila());
        assertEquals(2, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(2, koodis.size());

        Collections.sort(koodis, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiArvo().compareTo(o2.getKoodiArvo());
            }
        });

        KoodiType koodi1 = koodis.get(0);
        KoodiType koodi2 = koodis.get(1);

        assertEquals(koodiUri1, koodi1.getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodi1.getTila());
        assertEquals(2, koodi1.getVersio());
        assertEquals(newName1, KoodistoHelper.getKoodiMetadataForLanguage(koodi1, KieliType.FI).getNimi());

        assertEquals(koodiUri2, koodi2.getKoodiUri());
        assertEquals(TilaType.LUONNOS, koodi2.getTila());
        assertEquals(1, koodi2.getVersio());
        assertEquals(newName2, KoodistoHelper.getKoodiMetadataForLanguage(koodi2, KieliType.FI).getNimi());
    }

    @Test
    public void testSettingKoodiPassiveCreatesNewVersionOfKoodisto() {
        final String koodistoUri = "http://koodisto11";
        final int koodistoVersio = 1;

        final String koodiUri = "460";
        final int koodiVersio = 1;
        UpdateKoodiDataType updateData = new UpdateKoodiDataType();

        {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            assertEquals(koodistoVersio, koodisto.getVersio());
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());

            KoodiType koodi = getKoodiByUri(koodiUri);
            assertEquals(koodiVersio, koodi.getVersio());
            assertEquals(TilaType.HYVAKSYTTY, koodi.getTila());

            DataUtils.copyFields(koodi, updateData);
            updateData.setTila(UpdateKoodiTilaType.PASSIIVINEN);
        }
        koodiAdminService.updateKoodi(updateData);

        KoodistoType updatedKoodisto = getKoodistoByUri(koodistoUri);
        assertEquals(koodistoVersio + 1, updatedKoodisto.getVersio());
        assertEquals(TilaType.LUONNOS, updatedKoodisto.getTila());

        KoodiType previousKoodiVersio = getKoodiByUriAndVersio(koodiUri, koodiVersio);
        assertEquals(koodiVersio, previousKoodiVersio.getVersio());
        assertEquals(TilaType.HYVAKSYTTY, previousKoodiVersio.getTila());

        KoodiType updatedKoodi = getKoodiByUri(koodiUri);
        assertEquals(koodiVersio + 1, updatedKoodi.getVersio());
        assertEquals(TilaType.PASSIIVINEN, updatedKoodi.getTila());
    }

    @Test
    public void testNewVersionOfKoodiPreservesRelationsToo() {
        final String ylakoodiUri = "461";
        final String alakoodiUri = "462";

        final int ylakoodiVersio = 1;
        final int alakoodiVersio = 1;

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(ylakoodiVersio, ylakoodi.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(1, koodis.size());
            KoodiType alakoodi = koodis.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(alakoodiVersio, alakoodi.getVersio());

            DataUtils.copyFields(ylakoodi, updateData);
        }

        KoodiMetadataType fiMeta =
                KoodistoHelper.getKoodiMetadataForLanguage(updateData.getMetadata(), KieliType.FI);
        final String newName = "newname";
        assertFalse(fiMeta.getNimi().equals(newName));
        fiMeta.setNimi(newName);

        koodiAdminService.updateKoodi(updateData);
        assertEquals(1, koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodiUri, ylakoodiVersio),
                false, SuhteenTyyppiType.SISALTYY).size());

        List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodiUri, ylakoodiVersio + 1),
                false, SuhteenTyyppiType.SISALTYY);
        assertEquals(1, koodis.size());

        KoodiType alakoodi = koodis.get(0);
        assertEquals(alakoodiUri, alakoodi.getKoodiUri());
        assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
        assertEquals(alakoodiVersio, alakoodi.getVersio());
    }

    @Test
    public void testDeleteRelationUpdatesOtherRelationsToPointToTheNewVersion() {
        final String ylakoodiUri = "463";
        final String alakoodiUri1 = "464";
        final String alakoodiUri2 = "465";

        final int ylakoodiVersio = 1;
        final int ylakoodiVersioAfter = ylakoodiVersio + 1;
        final int alakoodiVersio1 = 1;
        final int alakoodiVersio2 = 1;

        final Comparator<KoodiType> comparator = new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiUri().compareTo(o2.getKoodiUri());
            }
        };

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(ylakoodiVersio, ylakoodi.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(2, koodis.size());

            Collections.sort(koodis, comparator);

            KoodiType alakoodi1 = koodis.get(0);
            assertEquals(alakoodiUri1, alakoodi1.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi1.getTila());
            assertEquals(alakoodiVersio1, alakoodi1.getVersio());

            KoodiType alakoodi2 = koodis.get(1);
            assertEquals(alakoodiUri2, alakoodi2.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi2.getTila());
            assertEquals(alakoodiVersio2, alakoodi2.getVersio());
        }

        koodiAdminService.removeRelationByAlakoodi(ylakoodiUri, Arrays.asList(alakoodiUri1),
                SuhteenTyyppiType.SISALTYY);

        {
            KoodiType previousYlakoodiVersion = getKoodiByUriAndVersio(ylakoodiUri, ylakoodiVersio);
            assertEquals(TilaType.HYVAKSYTTY, previousYlakoodiVersion.getTila());
            assertEquals(ylakoodiVersio, previousYlakoodiVersion.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(previousYlakoodiVersion),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(2, koodis.size());

            Collections.sort(koodis, comparator);

            KoodiType alakoodi1 = koodis.get(0);
            assertEquals(alakoodiUri1, alakoodi1.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi1.getTila());
            assertEquals(alakoodiVersio1, alakoodi1.getVersio());

            KoodiType alakoodi2 = koodis.get(1);
            assertEquals(alakoodiUri2, alakoodi2.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi2.getTila());
            assertEquals(alakoodiVersio2, alakoodi2.getVersio());
        }

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.LUONNOS, ylakoodi.getTila());
            assertEquals(ylakoodiVersioAfter, ylakoodi.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(1, koodis.size());
            KoodiType alakoodi2 = koodis.get(0);
            assertEquals(alakoodiUri2, alakoodi2.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi2.getTila());
            assertEquals(alakoodiVersio2, alakoodi2.getVersio());
        }
    }

    @Test
    public void testAddingNewRelationCreatesNewVersionOfParentKoodi() {
        final String ylakoodiUri = "461";
        final String alakoodiUri = "462";

        final String newAlakoodiUri = "466";

        final int ylakoodiVersio = 1;
        final int ylakoodiVersioAfter = ylakoodiVersio + 1;
        final int alakoodiVersio = 1;
        final int newAlakoodiVersio = 1;
        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(ylakoodiVersio, ylakoodi.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(1, koodis.size());

            KoodiType alakoodi = koodis.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(alakoodiVersio, alakoodi.getVersio());

            KoodiType newAlakoodi = getKoodiByUri(newAlakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, newAlakoodi.getTila());
            assertEquals(newAlakoodiVersio, newAlakoodi.getVersio());
        }

        koodiAdminService.addRelation(ylakoodiUri, newAlakoodiUri, SuhteenTyyppiType.SISALTYY);

        {
            KoodiType previousYlakoodiVersion = getKoodiByUriAndVersio(ylakoodiUri, ylakoodiVersio);
            assertEquals(TilaType.HYVAKSYTTY, previousYlakoodiVersion.getTila());
            assertEquals(ylakoodiVersio, previousYlakoodiVersion.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(previousYlakoodiVersion),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(1, koodis.size());

            KoodiType alakoodi = koodis.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(alakoodiVersio, alakoodi.getVersio());

            KoodiType newAlakoodi = getKoodiByUri(newAlakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, newAlakoodi.getTila());
            assertEquals(newAlakoodiVersio, newAlakoodi.getVersio());
        }

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.LUONNOS, ylakoodi.getTila());
            assertEquals(ylakoodiVersioAfter, ylakoodi.getVersio());

            List<KoodiType> koodis = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.SISALTYY);
            assertEquals(2, koodis.size());

            Collections.sort(koodis, new Comparator<KoodiType>() {
                @Override
                public int compare(KoodiType o1, KoodiType o2) {
                    return o1.getKoodiUri().compareTo(o2.getKoodiUri());
                }
            });

            KoodiType alakoodi = koodis.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(alakoodiVersio, alakoodi.getVersio());

            KoodiType newAlakoodi = koodis.get(1);
            assertEquals(newAlakoodiUri, newAlakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, newAlakoodi.getTila());
            assertEquals(alakoodiVersio, newAlakoodi.getVersio());
        }
    }

    @Test
    public void testAddingParallelRelationshipDoesNotCreateNewVersioOfKoodi() {
        final String ylakoodiUri = "466";
        final String alakoodiUri = "467";

        final int koodiVersio = 1;

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(koodiVersio, ylakoodi.getVersio());

            KoodiType alakoodi = getKoodiByUri(alakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(koodiVersio, alakoodi.getVersio());

            List<KoodiType> related = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.RINNASTEINEN);

            assertEquals(0, related.size());
        }

        koodiAdminService.addRelation(ylakoodiUri, alakoodiUri, SuhteenTyyppiType.RINNASTEINEN);

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(koodiVersio, ylakoodi.getVersio());

            List<KoodiType> related = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.RINNASTEINEN);
            assertEquals(1, related.size());

            KoodiType alakoodi = related.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(koodiVersio, alakoodi.getVersio());
        }
    }

    @Test
    public void testDeletingParallelRelationshipDoesNotCreateNewVersionOfKoodi() {
        final String ylakoodiUri = "468";
        final String alakoodiUri = "469";

        final int koodiVersio = 1;

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(koodiVersio, ylakoodi.getVersio());

            List<KoodiType> related = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.RINNASTEINEN);
            assertEquals(1, related.size());

            KoodiType alakoodi = related.get(0);
            assertEquals(alakoodiUri, alakoodi.getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(koodiVersio, alakoodi.getVersio());
        }

        koodiAdminService.removeRelationByAlakoodi(ylakoodiUri, Arrays.asList(alakoodiUri),
                SuhteenTyyppiType.RINNASTEINEN);

        {
            KoodiType ylakoodi = getKoodiByUri(ylakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, ylakoodi.getTila());
            assertEquals(koodiVersio, ylakoodi.getVersio());

            List<KoodiType> related = koodiService.listKoodiByRelation(createKoodiUriAndVersioType(ylakoodi),
                    false, SuhteenTyyppiType.RINNASTEINEN);
            assertEquals(0, related.size());

            KoodiType alakoodi = getKoodiByUri(alakoodiUri);
            assertEquals(TilaType.HYVAKSYTTY, alakoodi.getTila());
            assertEquals(koodiVersio, alakoodi.getVersio());
        }
    }

    private List<KoodiType> getKoodisByKoodistoVersio(String koodistoUri, Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchData =
                KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);
        return koodiService.searchKoodisByKoodisto(searchData);
    }

    @Test(expected = GenericFault.class)
    public void testUpdateKoodiNotInLatestKoodistoVersio() {
        final String koodistoUri = "http://koodisto15";
        final String koodiUri = "470";
        final int koodiVersio = 1;

        final int oldKoodistoVersio = 1;
        final int currentKoodistoVersio = 2;

        KoodiType koodi = null;
        {
            List<KoodiType> koodis = getKoodisByKoodistoVersio(koodistoUri, oldKoodistoVersio);
            assertEquals(1, koodis.size());
            assertEquals(koodiUri, koodis.get(0).getKoodiUri());
            assertEquals(koodiVersio, koodis.get(0).getVersio());
            koodi = koodis.get(0);

            KoodistoType latestKoodisto = getKoodistoByUri(koodistoUri);
            assertEquals(currentKoodistoVersio, latestKoodisto.getVersio());
            assertEquals(0, getKoodisByKoodistoVersio(koodistoUri, currentKoodistoVersio).size());
        }

        UpdateKoodiDataType updateData = new UpdateKoodiDataType();
        DataUtils.copyFields(koodi, updateData);
        updateData.setKoodiArvo("newArvo");

        try {
            koodiAdminService.updateKoodi(updateData);
        } catch (GenericFault e) {
            assertEquals(KoodiNotInKoodistoException.ERROR_KEY, e.getFaultInfo().getErrorCode());
            throw e;
        }
    }
}
