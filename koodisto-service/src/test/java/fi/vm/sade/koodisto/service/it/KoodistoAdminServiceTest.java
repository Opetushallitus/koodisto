package fi.vm.sade.koodisto.service.it;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiNotUniqueException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoAdminServiceTest {
    @Autowired
    private KoodistoAdminService koodistoAdminService;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    KoodistoService koodistoService;

    private List<KoodiType> listKoodisByKoodisto(String koodistoUri, Integer koodistoVersio) {
        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);
        return koodiService.searchKoodisByKoodisto(searchCriteria);
    }

    private KoodistoType getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        assertEquals(1, koodistos.size());
        return koodistos.get(0);
    }

    private KoodistoType getKoodistoByUriAndVersio(String koodistoUri, int koodistoVersio) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(
                koodistoUri, koodistoVersio);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        assertEquals(1, koodistos.size());

        return koodistos.get(0);
    }

    private List<KoodiType> getKoodisByKoodisto(String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchType =
                KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodistoUri);

        return koodiService.searchKoodisByKoodisto(searchType);
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
        List<KoodistoRyhmaListType> ryhmas = koodistoService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType("omistaja",
                "organisaatioOid", new Date(), null,
                "uusi luotu testikoodisto");
        KoodistoType created = koodistoAdminService.createKoodisto(ryhmaUris, createKoodistoDataType);

        KoodistoType fetched = getKoodistoByUri(created.getKoodistoUri());
        assertNotNull(fetched);
    }

    @Test(expected = GenericFault.class)
    public void testCreateWithNonUniqueNimi() {
        List<KoodistoRyhmaListType> ryhmas = koodistoService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        final String nimi = "testikoodistoFI";
        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType("omistaja",
                "organisaatioOid", new Date(), new Date(), nimi);

        try {
            koodistoAdminService.createKoodisto(ryhmaUris, createKoodistoDataType);
        } catch (GenericFault e) {
            assertEquals(KoodistoNimiNotUniqueException.ERROR_KEY, e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test
    public void testUpdate() {
        String koodistoUri = "http://www.kunnat.fi/kunta";

        KoodistoType dto = getKoodistoByUri(koodistoUri);
        assertEquals(100, listKoodisByKoodisto(koodistoUri, dto.getVersio()).size());

        int originalVersion = dto.getVersio();
        UpdateKoodistoDataType updateKoodistoData = new UpdateKoodistoDataType();

        DataUtils.copyFields(dto, updateKoodistoData);
        updateKoodistoData.setOmistaja("omistaja2");

        // this results in "minor" update - no new version
        koodistoAdminService.updateKoodisto(updateKoodistoData);
        KoodistoType updated = getKoodistoByUri(dto.getKoodistoUri());
        assertEquals("omistaja2", updated.getOmistaja());
        assertEquals(originalVersion, updated.getVersio());
        assertEquals(100, listKoodisByKoodisto(koodistoUri, updated.getVersio()).size());

        DataUtils.copyFields(updated, updateKoodistoData);
        for (KoodistoMetadataType mdDto : updateKoodistoData.getMetadataList()) {
            mdDto.setNimi(mdDto.getNimi() + "_new");
        }

        KoodistoType secondUpdated = koodistoAdminService.updateKoodisto(updateKoodistoData);
        assertNotSame(updated.getVersio(), secondUpdated.getVersio());
        assertEquals(100, listKoodisByKoodisto(koodistoUri, secondUpdated.getVersio()).size());
    }

    @Test(expected = GenericFault.class)
    public void testUpdateWithNonUniqueNimi() {
        String koodistoUriToUpdate = "http://testi.fi";
        KoodistoType koodistoToUpdate = getKoodistoByUri(koodistoUriToUpdate);

        // Let's first update the koodisto without changing any field. This
        // should not cause an exception.
        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        updateData.setKoodistoUri(koodistoToUpdate.getKoodistoUri());
        updateData.setLukittu(koodistoToUpdate.isLukittu());
        updateData.setOmistaja(koodistoToUpdate.getOmistaja());
        updateData.setOrganisaatioOid(koodistoToUpdate.getOrganisaatioOid());
        updateData.setTila(koodistoToUpdate.getTila());
        updateData.setVoimassaAlkuPvm(koodistoToUpdate.getVoimassaAlkuPvm());
        updateData.setVoimassaLoppuPvm(koodistoToUpdate.getVoimassaLoppuPvm());
        updateData.getMetadataList().addAll(koodistoToUpdate.getMetadataList());
        updateData.setVersio(koodistoToUpdate.getVersio());
        updateData.setLockingVersion(koodistoToUpdate.getLockingVersion());

        try {
            koodistoToUpdate = koodistoAdminService.updateKoodisto(updateData);
        } catch (Exception e) {
            fail();
        }

        // Now let's change the nimi into something non-unique.
        String kunnatUri = "http://www.kunnat.fi/kunta";
        KoodistoType kunnat = getKoodistoByUri(kunnatUri);
        String nimi = kunnat.getMetadataList().get(0).getNimi();

        for (KoodistoMetadataType m : updateData.getMetadataList()) {
            m.setNimi(nimi);
        }

        updateData.setVersio(koodistoToUpdate.getVersio());
        updateData.setLockingVersion(koodistoToUpdate.getLockingVersion());

        try {
            koodistoAdminService.updateKoodisto(updateData);
        } catch (GenericFault e) {
            assertEquals(KoodistoNimiNotUniqueException.ERROR_KEY, e.getFaultInfo().getErrorCode());
            throw e;
        }
    }

    @Test
    public void testUpdateWithInsufficientMetadataFields() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        KoodistoType koodistoToUpdate = getKoodistoByUri(koodistoUri);
        assertEquals(3, koodistoToUpdate.getMetadataList().size());

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        updateData.setKoodistoUri(koodistoToUpdate.getKoodistoUri());
        updateData.setLukittu(koodistoToUpdate.isLukittu());
        updateData.setOmistaja(koodistoToUpdate.getOmistaja());
        updateData.setOrganisaatioOid(koodistoToUpdate.getOrganisaatioOid());
        updateData.setTila(koodistoToUpdate.getTila());
        updateData.setVoimassaAlkuPvm(koodistoToUpdate.getVoimassaAlkuPvm());
        updateData.setVoimassaLoppuPvm(koodistoToUpdate.getVoimassaLoppuPvm());

        KoodistoMetadataType enMeta = null;
        for (KoodistoMetadataType m : koodistoToUpdate.getMetadataList()) {
            if (KieliType.EN.equals(m.getKieli())) {
                enMeta = m;
            }
            updateData.getMetadataList().add(m);
        }

        enMeta.setNimi("");

        boolean caughtOne = false;
        try {
            koodistoAdminService.updateKoodisto(updateData);
        } catch (GenericFault e) {
            caughtOne = true;
            assertEquals(KoodistoNimiEmptyException.class.getCanonicalName(), e.getFaultInfo().getErrorCode());
        } catch (Exception e) {
            fail();
        }

        assertTrue(caughtOne);
    }

    @Test
    public void testUpdateWithoutSendingMetadataForAllLanguages() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        KoodistoType koodistoToUpdate = getKoodistoByUri(koodistoUri);
        assertEquals(3, koodistoToUpdate.getMetadataList().size());

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        updateData.setKoodistoUri(koodistoToUpdate.getKoodistoUri());
        updateData.setLukittu(koodistoToUpdate.isLukittu());
        updateData.setOmistaja(koodistoToUpdate.getOmistaja());
        updateData.setOrganisaatioOid(koodistoToUpdate.getOrganisaatioOid());
        updateData.setTila(koodistoToUpdate.getTila());
        updateData.setVoimassaAlkuPvm(koodistoToUpdate.getVoimassaAlkuPvm());
        updateData.setVoimassaLoppuPvm(koodistoToUpdate.getVoimassaLoppuPvm());
        updateData.setVersio(koodistoToUpdate.getVersio());
        updateData.setLockingVersion(koodistoToUpdate.getLockingVersion());

        for (KoodistoMetadataType m : koodistoToUpdate.getMetadataList()) {
            if (!KieliType.EN.equals(m.getKieli())) {
                updateData.getMetadataList().add(m);
            }
        }

        assertEquals(2, updateData.getMetadataList().size());

        koodistoAdminService.updateKoodisto(updateData);

        KoodistoType updatedKoodisto = getKoodistoByUri(koodistoUri);
        assertEquals(2, updatedKoodisto.getMetadataList().size());
    }

    @Test(expected = RuntimeException.class)
    public void testDeletePassiveKoodisto() {
        final String koodistoUri = "http://passiivinenkoodistojossakoodeja";
        final int koodistoVersio = 1;

        KoodistoType koodisto = getKoodistoByUriAndVersio(koodistoUri, koodistoVersio);
        assertNotNull(koodisto);
        assertEquals(TilaType.PASSIIVINEN, koodisto.getTila());
        koodistoAdminService.deleteKoodistoVersion(koodistoUri, koodistoVersio);
        getKoodistoByUriAndVersio(koodistoUri, koodistoVersio);
    }

    @Test(expected = GenericFault.class)
    public void testDeleteNonPassiveKoodisto() {
        final String koodistoUri = "http://testikoodisto.fi";
        final int koodistoVersio = 1;

        KoodistoType koodisto = getKoodistoByUriAndVersio(koodistoUri, koodistoVersio);
        assertNotNull(koodisto);
        assertFalse(TilaType.PASSIIVINEN.equals(koodisto.getTila()));
        try {
            koodistoAdminService.deleteKoodistoVersion(koodistoUri, koodistoVersio);
        } catch (GenericFault e) {
            assertEquals(KoodistoVersioNotPassiivinenException.class.getCanonicalName(), e.getFaultInfo()
                    .getErrorCode());
            throw e;
        }
    }

    @Test
    public void testChangingKoodistoNimiCreatesNewVersio() {
        final String koodistoUri = "http://koodisto2";
        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());

        int versioBefore = koodisto.getVersio();
        KoodistoMetadataType fiMeta = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.FI);

        final String uusiNimi = "uusinimi";
        assertTrue(!fiMeta.getNimi().equals(uusiNimi));
        fiMeta.setNimi(uusiNimi);

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);

        koodistoAdminService.updateKoodisto(updateData);

        KoodistoType updatedKoodisto = getKoodistoByUri(koodistoUri);
        assertEquals(versioBefore + 1, updatedKoodisto.getVersio());
    }

    @Test
    public void testCreatingNewVersioCreatesNewVersioOfAllTheKoodis() {
        final String koodistoUri = "http://koodisto3";
        final int versioBefore = 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);

        {
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(versioBefore, koodisto.getVersio());

            List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
            assertEquals(3, koodis.size());

            for (KoodiType k : koodis) {
                assertEquals(versioBefore, k.getVersio());
                assertEquals(TilaType.HYVAKSYTTY, k.getTila());
            }
        }

        UpdateKoodistoDataType updateData = DataUtils.createUpdateKoodistoDataType(koodistoUri, "omistaja",
                TilaType.HYVAKSYTTY, "organisaatiooid", new Date(), null, "uusinimikoodistolle", koodisto.getVersio(), koodisto.getLockingVersion());
        koodistoAdminService.updateKoodisto(updateData);
        koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, koodisto.getTila());
        assertEquals(versioBefore + 1, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(3, koodis.size());

        for (KoodiType k : koodis) {
            assertEquals(versioBefore + 1, k.getVersio());
            assertEquals(TilaType.LUONNOS, k.getTila());
        }
    }

    @Test
    public void testAcceptingKoodistoAcceptsAllKoodisInKoodisto() {
        final String koodistoUri = "http://koodisto5";
        final int versioBefore = 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);

        {
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

            assertEquals("451", koodis.get(0).getKoodiUri());
            assertEquals(TilaType.LUONNOS, koodis.get(0).getTila());
            assertEquals(versioBefore, koodis.get(0).getVersio());

            assertEquals("452", koodis.get(1).getKoodiUri());
            assertEquals(TilaType.LUONNOS, koodis.get(1).getTila());
            assertEquals(versioBefore, koodis.get(1).getVersio());

            assertEquals("453", koodis.get(2).getKoodiUri());
            assertEquals(TilaType.HYVAKSYTTY, koodis.get(2).getTila());
            assertEquals(versioBefore, koodis.get(2).getVersio());
        }

        UpdateKoodistoDataType updateData = DataUtils.createUpdateKoodistoDataType(koodistoUri, "omistaja",
                TilaType.HYVAKSYTTY, "organisaatiooid", new Date(), null, "koodistonnimi", koodisto.getVersio(), koodisto.getLockingVersion());
        koodistoAdminService.updateKoodisto(updateData);

        koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
        assertEquals(versioBefore, koodisto.getVersio());

        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(3, koodis.size());

        Collections.sort(koodis, new Comparator<KoodiType>() {
            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return o1.getKoodiUri().compareTo(o2.getKoodiUri());
            }
        });

        assertEquals("451", koodis.get(0).getKoodiUri());
        assertEquals(TilaType.HYVAKSYTTY, koodis.get(0).getTila());
        assertEquals(versioBefore, koodis.get(0).getVersio());

        assertEquals("452", koodis.get(1).getKoodiUri());
        assertEquals(TilaType.HYVAKSYTTY, koodis.get(1).getTila());
        assertEquals(versioBefore, koodis.get(1).getVersio());

        assertEquals("453", koodis.get(2).getKoodiUri());
        assertEquals(TilaType.HYVAKSYTTY, koodis.get(2).getTila());
        assertEquals(versioBefore, koodis.get(2).getVersio());

    }

    @Test
    public void testUpdatingDraftKoodistoNimiSetsUpdatedAtDate() {
        final String koodistoUri = "http://koodisto7";
        final int versio = 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        Date now = new Date();

        {
            assertEquals(TilaType.LUONNOS, koodisto.getTila());
            assertEquals(versio, koodisto.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodisto.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }

        final String newNimi = "uusinimi";
        KoodistoMetadataType fiMeta = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.FI);
        assertFalse(newNimi.equals(fiMeta.getNimi()));
        fiMeta.setNimi(newNimi);

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);

        koodistoAdminService.updateKoodisto(updateData);
        KoodistoType updated = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, updated.getTila());
        assertEquals(versio, updated.getVersio());

        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());
        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testUpdatingKoodistoMetadataSetsUpdatedAtDate() {
        final String koodistoUri = "http://koodisto3";
        final int versio = 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        Date now = new Date();

        {
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(versio, koodisto.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodisto.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }

        final String newKasite = "uusikasite";
        KoodistoMetadataType fiMeta = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.FI);
        assertNull(fiMeta.getKasite());
        fiMeta.setKasite(newKasite);

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);

        koodistoAdminService.updateKoodisto(updateData);
        KoodistoType updated = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.HYVAKSYTTY, updated.getTila());
        assertEquals(versio, updated.getVersio());

        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());
        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testUpdatingAcceptedKoodistoNimiSetsUpdatedAtDate() {
        final String koodistoUri = "http://koodisto13";
        final int versio = 1;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        Date now = new Date();

        {
            assertEquals(TilaType.HYVAKSYTTY, koodisto.getTila());
            assertEquals(versio, koodisto.getVersio());
            Date updatedAtDate = DateHelper.xmlCalToDate(koodisto.getPaivitysPvm());
            assertTrue(updatedAtDate.before(now));
        }

        final String newNimi = "uusinimi";
        KoodistoMetadataType fiMeta = KoodistoHelper.getKoodistoMetadataForLanguage(koodisto, KieliType.FI);
        assertFalse(newNimi.equals(fiMeta.getNimi()));
        fiMeta.setNimi(newNimi);

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);

        koodistoAdminService.updateKoodisto(updateData);
        KoodistoType updated = getKoodistoByUri(koodistoUri);
        assertEquals(TilaType.LUONNOS, updated.getTila());
        assertEquals(versio + 1, updated.getVersio());

        Date updatedAtDate = DateHelper.xmlCalToDate(updated.getPaivitysPvm());
        assertTrue(DataUtils.datePartIsEqual(now, updatedAtDate));
    }

    @Test
    public void testAcceptingDraftKoodistoSetsEndDateForPreviousVersionOfTheKoodisto() {
        final String koodistoUri = "http://koodisto6";
        final int versio = 2;
        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        {
            assertEquals(TilaType.LUONNOS, koodisto.getTila());
            assertEquals(versio, koodisto.getVersio());

            KoodistoType previous = getKoodistoByUriAndVersio(koodistoUri, versio - 1);
            assertEquals(versio - 1, previous.getVersio());
            assertEquals(TilaType.HYVAKSYTTY, previous.getTila());
            assertNull(previous.getVoimassaLoppuPvm());
        }

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);
        // Let's set the state to accepted
        updateData.setTila(TilaType.HYVAKSYTTY);

        koodistoAdminService.updateKoodisto(updateData);

        // Let's get the previous version
        KoodistoType previous = getKoodistoByUriAndVersio(koodistoUri, versio - 1);
        assertEquals(versio - 1, previous.getVersio());
        assertEquals(TilaType.HYVAKSYTTY, previous.getTila());


        // Check that the end date is set to this day
        Date now = new Date();
        assertNotNull(previous.getVoimassaLoppuPvm());
        Date endDate = DateHelper.xmlCalToDate(previous.getVoimassaLoppuPvm());
        assertTrue(DataUtils.datePartIsEqual(now, endDate));
    }

    @Test
    public void testAcceptingKoodistoSetsEndDateForPreviousVersionsOfTheKoodisInKoodisto() {
        final String koodistoUri = "http://koodisto6";
        final int koodistoVersio = 2;
        final String koodiUri = "454";
        final int koodiVersio = 2;
        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        List<KoodiType> koodis = getKoodisByKoodisto(koodistoUri);
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);
        {
            assertEquals(TilaType.LUONNOS, koodisto.getTila());
            assertEquals(koodistoVersio, koodisto.getVersio());

            KoodistoType previous = getKoodistoByUriAndVersio(koodistoUri, koodistoVersio - 1);
            assertEquals(koodistoVersio - 1, previous.getVersio());
            assertEquals(TilaType.HYVAKSYTTY, previous.getTila());
            assertNull(previous.getVoimassaLoppuPvm());

            assertEquals(koodiUri, koodi.getKoodiUri());
            assertEquals(TilaType.LUONNOS, koodi.getTila());
            assertEquals(koodiVersio, koodi.getVersio());

            KoodiType previousKoodi = getKoodiByUriAndVersio(koodiUri, koodiVersio - 1);
            assertEquals(koodiVersio - 1, previousKoodi.getVersio());
            assertEquals(TilaType.HYVAKSYTTY, previousKoodi.getTila());
            assertNull(previousKoodi.getVoimassaLoppuPvm());
        }

        UpdateKoodistoDataType updateData = new UpdateKoodistoDataType();
        DataUtils.copyFields(koodisto, updateData);
        // Let's set the state to accepted
        updateData.setTila(TilaType.HYVAKSYTTY);

        koodistoAdminService.updateKoodisto(updateData);

        // Let's get the previous version
        KoodiType previousKoodi = getKoodiByUriAndVersio(koodiUri, koodiVersio - 1);
        assertEquals(koodiVersio - 1, previousKoodi.getVersio());
        assertEquals(TilaType.HYVAKSYTTY, previousKoodi.getTila());

    }
}
