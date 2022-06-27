package fi.vm.sade.koodisto.service.business.it;

import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.repository.KoodiVersioRepository;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:truncate_tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SpringBootTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@WithMockUser(value = "1.2.3.4.5", authorities = "APP_KOODISTO_CRUD_1.2.246.562.10.00000000001")
public class KoodiBusinessServiceTest {

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiVersioRepository koodiVersioRepository;

    private static UpdateKoodiDataType convert(KoodiVersio koodiVersio) throws Exception {

        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();

        updateKoodiDataType.setKoodiArvo(koodiVersio.getKoodiarvo());
        updateKoodiDataType.setKoodiUri(koodiVersio.getKoodi().getKoodiUri());
        updateKoodiDataType.setVoimassaAlkuPvm(koodiVersio.getVoimassaAlkuPvm());
        updateKoodiDataType.setVoimassaLoppuPvm(koodiVersio.getVoimassaLoppuPvm());
        updateKoodiDataType.getMetadata().clear();
        updateKoodiDataType.getMetadata().addAll(convert(koodiVersio.getMetadatas()));
        updateKoodiDataType.setVersio(koodiVersio.getVersio());
        updateKoodiDataType.setLockingVersion(koodiVersio.getVersion());

        return updateKoodiDataType;
    }

    private static Set<KoodiMetadataType> convert(Set<KoodiMetadata> koodiMetadataSet) {

        Set<KoodiMetadataType> koodiMetadataTypeSet = new HashSet<>();

        for (KoodiMetadata koodiMetadata : koodiMetadataSet) {

            KoodiMetadataType koodiMetadataType = new KoodiMetadataType();

            koodiMetadataType.setKieli(KieliType.valueOf(koodiMetadata.getKieli().name()));
            koodiMetadataType.setNimi(koodiMetadata.getNimi());
            koodiMetadataType.setLyhytNimi(koodiMetadata.getLyhytNimi());
            koodiMetadataType.setKuvaus(koodiMetadata.getKuvaus());

            koodiMetadataType.setKayttoohje(koodiMetadata.getKayttoohje());
            koodiMetadataType.setKasite(koodiMetadata.getKasite());
            koodiMetadataType.setHuomioitavaKoodi(koodiMetadata.getHuomioitavaKoodi());
            koodiMetadataType.setSisaltaaMerkityksen(koodiMetadata.getSisaltaaMerkityksen());
            koodiMetadataType.setEiSisallaMerkitysta(koodiMetadata.getEiSisallaMerkitysta());
            koodiMetadataType.setSisaltaaKoodiston(koodiMetadata.getSisaltaaKoodiston());

            koodiMetadataTypeSet.add(koodiMetadataType);
        }

        return koodiMetadataTypeSet;
    }

    @Test
    public void testCreate() {
        KoodistoVersio koodistoVersio = createKoodisto("omistaja", "organisaatioOid", new Date(), null,
                "uusi luotu testikoodisto");
        KoodiVersioWithKoodistoItem koodi = createKoodi(koodistoVersio, "arvo", "koodi");
        assertNotNull(koodi.getKoodiVersio().getId());
        assertNotNull(koodi.getKoodiVersio().getKoodi());
        assertNotNull(koodi.getKoodiVersio().getKoodi().getId());

        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoVersio.getKoodisto().getKoodistoUri());
        assertEquals(1, koodiBusinessService.searchKoodis(searchCriteria).size());
    }

    @Test
    public void testDeleteVersio() {
        final String koodistoUri = "koodisto21";
        final String koodiUri = "477";

        final int numberOfVersiosBeforeDelete = 2;
        final int numberOfVersiosAfterDelete = 1;

        final List<KoodiVersioWithKoodistoItem> before = listByUri(koodiUri);
        assertEquals(numberOfVersiosBeforeDelete, before.size());

        koodiBusinessService.delete(koodiUri, 2);

        KoodistoVersio koodistoVersio = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        final List<KoodiVersioWithKoodistoItem> afterFirstDelete = listByUri(koodiUri);

        assertEquals(Tila.LUONNOS, koodistoVersio.getTila());
        assertEquals(numberOfVersiosBeforeDelete, afterFirstDelete.size());

        koodiBusinessService.delete(koodiUri, 1, true);

        final List<KoodiVersioWithKoodistoItem> afterSecondDelete = listByUri(koodiUri);
        assertEquals(numberOfVersiosAfterDelete, afterSecondDelete.size());
    }

    @Test
    public void changeTilaFromHyvaksyttyToLuonnos() throws Exception {

        final String koodistoUri = "koodisto19";
        final Integer koodistoVersioBeforeTilaChange = 1;
        final Integer koodistoVersioAfterTilaChange = 2;


        KoodistoVersio beforeUpdate = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        assertEquals(koodistoVersioBeforeTilaChange, beforeUpdate.getVersio());
        assertEquals(Tila.HYVAKSYTTY, beforeUpdate.getTila());

        List<KoodiVersioWithKoodistoItem> koodiVersioWithKoodistoItems = koodiBusinessService.getKoodisByKoodisto("koodisto19", true);
        for (KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem : koodiVersioWithKoodistoItems) {
            if (Tila.HYVAKSYTTY.equals(koodiVersioWithKoodistoItem.getKoodiVersio().getTila())) {
                UpdateKoodiDataType updateKoodiDataType = convert(koodiVersioWithKoodistoItem.getKoodiVersio());
                updateKoodiDataType.setTila(UpdateKoodiTilaType.LUONNOS);
                koodiBusinessService.updateKoodi(updateKoodiDataType);
            }
        }

        KoodistoVersio afterUpdate = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        assertEquals(Tila.LUONNOS, afterUpdate.getTila());
        assertEquals(koodistoVersioAfterTilaChange, afterUpdate.getVersio());
    }

    @Test
    public void setsEndDatePreviousVersionWhenNewVersionIsSetToHyvaksytty() {
        KoodiVersio latest = koodiBusinessService.getLatestKoodiVersio("436");
        KoodiVersio updated = koodiBusinessService.createNewVersion(latest.getKoodi().getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, latest.getTila());
        assertNull(latest.getVoimassaLoppuPvm());
        assertEquals(Tila.LUONNOS, updated.getTila());
        koodiBusinessService.setKoodiTila(updated.getKoodi().getKoodiUri(), TilaType.HYVAKSYTTY);
        latest = koodiVersioRepository.findById(latest.getId()).orElseThrow();
        assertNotNull(latest.getVoimassaLoppuPvm());
    }

    @Test
    public void koodiIsCopiedIntoNewVersionOfKoodistoWhenNewKoodiIsAdded() {
        CreateKoodiDataType createKoodiData = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodiDataType("new",
                new Date(), null, "uusi");
        koodiBusinessService.createKoodi("koodisiirtyykoodisto", createKoodiData);
        List<KoodiVersioWithKoodistoItem> items = koodiBusinessService.getKoodisByKoodistoVersio("koodisiirtyykoodisto", 2, true);
        assertEquals(2, items.size());
        assertNotNull(koodiBusinessService.getKoodiByKoodistoVersio("koodisiirtyykoodisto", 2, "koodisiirtyy"));
        assertNotNull(koodiBusinessService.getKoodiByKoodistoVersio("koodisiirtyykoodisto", 2, "koodisiirtyykoodisto_new"));
    }

    @Test
    public void koodiVersioShouldNotBeLatest() {
        assertFalse(koodiBusinessService.isLatestKoodiVersio("455", 2));
    }

    @Test
    public void koodiVersioShouldBeLatest() {
        assertTrue(koodiBusinessService.isLatestKoodiVersio("455", 4));
    }

    @Test
    public void shouldFetchExactKoodiVersio() {
        assertEquals(2, koodiBusinessService.getKoodiVersio("455", 2).getVersio().intValue());
    }

    @Test
    public void doesNotCopyPassiveRelationsWhenNewVersionIsCreated() {
        String koodiUri = "passiivisuhdeeikopioidu";
        koodiBusinessService.createNewVersion(koodiUri);
        assertTrue(koodiBusinessService.listByRelation(koodiUri, true, SuhteenTyyppi.SISALTYY).isEmpty());
    }

    @Test
    public void fetchesKoodiAndInitializesKoodiVersions() {
        Koodi koodi = koodiBusinessService.getKoodi("435");
        assertNotNull(koodi);
        assertTrue(Hibernate.isInitialized(koodi.getKoodiVersios()));
        for (KoodiVersio kv : koodi.getKoodiVersios()) {
            assertTrue(Hibernate.isInitialized(kv));
            assertTrue(Hibernate.isInitialized(kv.getMetadatas()));
            assertTrue(Hibernate.isInitialized(kv.getAlakoodis()));
            assertTrue(Hibernate.isInitialized(kv.getYlakoodis()));
            assertFalse(kv.getMetadatas().isEmpty());
        }
    }

    @Test
    @Transactional
    public void setsRelationsToPassiveWhenUpdatingTilaToPassive() throws Exception {
        KoodiVersio latest = koodiBusinessService.getLatestKoodiVersio("809suhdetahan");
        UpdateKoodiDataType updateData = convert(latest);
        updateData.setTila(UpdateKoodiTilaType.PASSIIVINEN);
        koodiBusinessService.updateKoodi(updateData);
        KoodiVersio newest = koodiBusinessService.getLatestKoodiVersio("809suhdetahan");
        assertRelationsArePassive(newest, true);
    }

    @Test
    public void deletingLatestVersionActivatesPreviousRelations() {
        koodiBusinessService.delete("wanhasuhdeaktivoituupoistossa", 2);
        Koodi koodi = koodiBusinessService.getKoodi("wanhasuhdeaktivoituupoistossa");
        assertEquals(1, koodi.getKoodiVersios().size());
        KoodiVersio kv = koodi.getKoodiVersios().iterator().next();
        assertEquals(1, kv.getVersio().intValue());
        assertEquals(1, kv.getAlakoodis().size());
        assertRelationsArePassive(kv, false);
    }

    @Test
    public void getKoodi() {
        assertNotNull(koodiBusinessService.getKoodi("3", 1));
    }

    @Test(expected = KoodiNotFoundException.class)
    public void getKoodiNotFound() {
        koodiBusinessService.getKoodi("this-should-not-exists", 0);
    }

    public void assertRelationsArePassive(KoodiVersio latest, boolean passive) {
        for (KoodinSuhde ks : latest.getAlakoodis()) {
            assertEquals(passive, ks.isPassive());
        }
        for (KoodinSuhde ks : latest.getYlakoodis()) {
            assertEquals(passive, ks.isPassive());
        }
    }

    private List<KoodiVersioWithKoodistoItem> listByUri(String koodiUri) {
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(koodiUri);
        return koodiBusinessService.searchKoodis(searchType);
    }

    private KoodiVersioWithKoodistoItem createKoodi(KoodistoVersio koodisto, String koodiArvo, String nimi) {
        CreateKoodiDataType createKoodiData = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodiDataType(koodiArvo,
                new Date(), null, nimi);

        KoodiVersioWithKoodistoItem koodi = koodiBusinessService.createKoodi(koodisto.getKoodisto().getKoodistoUri(), createKoodiData);
        return koodi;
    }

    private KoodistoVersio createKoodisto(String omistaja, String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        List<KoodistoRyhma> ryhmas = koodistoBusinessService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoDataType = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodistoDataType(
                omistaja, organisaatioOid, voimassaAlkuPvm, voimassaLoppuPvm, nimi);
        KoodistoVersio koodisto = koodistoBusinessService.createKoodisto(ryhmaUris, createKoodistoDataType);
        return koodisto;
    }

}
