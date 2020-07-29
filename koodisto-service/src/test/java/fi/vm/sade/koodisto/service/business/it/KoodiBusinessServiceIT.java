package fi.vm.sade.koodisto.service.business.it;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.repository.KoodiVersioRepository;
import fi.vm.sade.koodisto.service.business.DownloadBusinessService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static org.junit.Assert.*;

@TestPropertySource(locations = "classpath:application.properties")
@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class
})
@Transactional
@WithMockUser("1.2.3.4.5")
public class KoodiBusinessServiceIT {

    @MockBean
    private DownloadBusinessService downloadBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;
    
    @Autowired
    private KoodiVersioRepository koodiVersioRepository;

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
        final String koodistoUri = "http://koodisto21";
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
        
        final String koodistoUri = "http://koodisto19";
        final Integer koodistoVersioBeforeTilaChange = 1;
        final Integer koodistoVersioAfterTilaChange = 2;
        
        
        KoodistoVersio beforeUpdate = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        assertEquals(koodistoVersioBeforeTilaChange, beforeUpdate.getVersio());
        assertEquals(Tila.HYVAKSYTTY, beforeUpdate.getTila());
        
        List<KoodiVersioWithKoodistoItem> koodiVersioWithKoodistoItems =  koodiBusinessService.getKoodisByKoodisto("http://koodisto19", true);
        for(KoodiVersioWithKoodistoItem koodiVersioWithKoodistoItem : koodiVersioWithKoodistoItems) {
            if(Tila.HYVAKSYTTY.equals(koodiVersioWithKoodistoItem.getKoodiVersio().getTila())) {
                UpdateKoodiDataType updateKoodiDataType = convert(koodiVersioWithKoodistoItem.getKoodiVersio());;
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
    public void setsRelationsToPassiveWhenUpdatingTilaToPassive() throws Exception {
        KoodiVersio latest = koodiBusinessService.getLatestKoodiVersio("809suhdetahan");
        UpdateKoodiDataType updateData = convert(latest);
        updateData.setTila(UpdateKoodiTilaType.PASSIIVINEN);
        koodiBusinessService.updateKoodi(updateData);
        assertRelationsArePassive(koodiBusinessService.getLatestKoodiVersio("809suhdetahan"), true);
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

    private void assertRelationsArePassive(KoodiVersio latest, boolean passive) {
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

    private static UpdateKoodiDataType convert(KoodiVersio koodiVersio) throws Exception {

        UpdateKoodiDataType updateKoodiDataType = new UpdateKoodiDataType();

        updateKoodiDataType.setKoodiArvo(koodiVersio.getKoodiarvo());
        updateKoodiDataType.setKoodiUri(koodiVersio.getKoodi().getKoodiUri());
        updateKoodiDataType.setVoimassaAlkuPvm(convert(koodiVersio.getVoimassaAlkuPvm()));
        updateKoodiDataType.setVoimassaLoppuPvm(convert(koodiVersio.getVoimassaLoppuPvm()));
        updateKoodiDataType.getMetadata().clear();
        updateKoodiDataType.getMetadata().addAll(convert(koodiVersio.getMetadatas()));
        updateKoodiDataType.setVersio(koodiVersio.getVersio());
        updateKoodiDataType.setLockingVersion(koodiVersio.getVersion());

        return updateKoodiDataType;
    }

    private static XMLGregorianCalendar convert(Date date) throws Exception {

        if(date == null) return null;

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

    }

    private static Set<KoodiMetadataType> convert(Set<KoodiMetadata> koodiMetadataSet) {

        Set<KoodiMetadataType> koodiMetadataTypeSet = new HashSet<KoodiMetadataType>();

        for(KoodiMetadata koodiMetadata : koodiMetadataSet) {

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

}
