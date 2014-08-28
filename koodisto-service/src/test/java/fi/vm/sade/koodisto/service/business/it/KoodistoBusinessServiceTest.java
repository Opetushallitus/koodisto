package fi.vm.sade.koodisto.service.business.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.dao.KoodistonSuhdeDAO;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioHasRelationsException;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.business.exception.KoodistonSuhdeContainsKoodinSuhdeException;
import fi.vm.sade.koodisto.service.business.exception.KoodistosAlreadyHaveSuhdeException;
import fi.vm.sade.koodisto.service.it.DataUtils;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class KoodistoBusinessServiceTest {

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistonSuhdeDAO suhdeDAO;

    private final static Long KOODISTON_SUHDE = 6l;

    @Test
    public void testCreate() {
        KoodistoVersio koodistoVersio = createKoodisto("omistaja", "organisaatioOid", new Date(), null,
                "uusi luotu testikoodisto");
        assertNotNull(koodistoVersio);
        assertNotNull(koodistoVersio.getId());
        assertNotNull(koodistoVersio.getKoodisto());
        assertNotNull(koodistoVersio.getKoodisto().getId());
    }

    private List<KoodistoVersio> listAllKoodistoVersions(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.koodistoVersiosByUri(koodistoUri);
        return koodistoBusinessService.searchKoodistos(searchType);
    }

    private KoodistoVersio getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);
        List<KoodistoVersio> koodistos = koodistoBusinessService.searchKoodistos(searchType);
        assertEquals(1, koodistos.size());
        return koodistos.get(0);
    }

    @Test
    public void testCreateNewVersion() {
        String koodistoUri = "http://www.kunnat.fi/kunta";

        List<KoodistoVersio> currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(1, currentVersions.size());
        KoodistoVersio newVersion = koodistoBusinessService.createNewVersion(koodistoUri);
        assertNotSame(currentVersions.get(0).getVersio(), newVersion.getVersio());
        currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(2, currentVersions.size());
    }

    @Test
    public void testListAllVersions() {

        String uri = "http://www.kunnat.fi/kunta";

        List<KoodistoVersio> koodistos = listAllKoodistoVersions(uri);
        assertEquals(1, koodistos.size());
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

    @Test(expected = KoodiVersioHasRelationsException.class)
    public void testDeleteVersioWithKoodiVersioHasRelationsException() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Integer koodistoVersio = 2;

        final int numberOfVersiosBeforeDelete = 2;
        final int numberOfVersiosAfterDelete = 2;

        KoodistoVersio koodisto = getKoodistoByUri(koodistoUri);
        final List<KoodistoVersio> before = listAllKoodistoVersions(koodisto.getKoodisto().getKoodistoUri());
        assertEquals(numberOfVersiosBeforeDelete, before.size());

        koodistoBusinessService.delete(koodistoUri, koodistoVersio);

        koodisto = getKoodistoByUri(koodistoUri);
        final List<KoodistoVersio> after = listAllKoodistoVersions(koodisto.getKoodisto().getKoodistoUri());
        assertEquals(numberOfVersiosAfterDelete, after.size());
    }

    @Test
    public void testDeleteKoodisto() {

        final String koodistoUri = "http://koodisto20";
        final Integer koodistoVersio = 1;

        assertTrue(koodistoBusinessService.koodistoExists(koodistoUri));

        try {
            koodistoBusinessService.delete(koodistoUri, koodistoVersio);
        } catch (KoodiVersioNotPassiivinenException e) {
        }

        assertTrue(koodistoBusinessService.koodistoExists(koodistoUri));

        koodiBusinessService.delete("478", 1, true);

        try {
            koodistoBusinessService.delete(koodistoUri, koodistoVersio);
        } catch (KoodiVersioNotPassiivinenException e) {
        }

        assertFalse(koodistoBusinessService.koodistoExists(koodistoUri));

    }

    @Test(expected = KoodistonSuhdeContainsKoodinSuhdeException.class)
    public void existingCodeElementRelationsPreventDeletingKoodisto() {
        koodistoBusinessService.removeRelation("koodisiirtyykoodisto", Arrays.asList("http://koodisto18"), SuhteenTyyppi.SISALTYY);
    }

    @Test
    public void removeRelation() {
        assertNotNull(suhdeDAO.read(KOODISTON_SUHDE));
        koodistoBusinessService.removeRelation("suhde502kanssa", Arrays.asList("suhde501kanssa"), SuhteenTyyppi.SISALTYY);
        assertNull(suhdeDAO.read(KOODISTON_SUHDE));
    }

    @Test
    public void addsRelation() {
        koodistoBusinessService.addRelation("http://koodisto20", "http://koodisto21", SuhteenTyyppi.RINNASTEINEN);
        assertTrue(koodistoBusinessService.hasAnyRelation("http://koodisto20", "http://koodisto21"));
    }

    @Test(expected = KoodistosAlreadyHaveSuhdeException.class)
    public void preventsAddingSameRelationMoreThanOnce() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde501kanssa", SuhteenTyyppi.SISALTYY);
    }

    @Test(expected = KoodistosAlreadyHaveSuhdeException.class)
    public void preventsAddingSameRelationMoreThanOnceDespiteRelationType() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde501kanssa", SuhteenTyyppi.RINNASTEINEN);
    }

    @Test
    public void addsRelationThatReferencesKoodistoItself() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.SISALTYY);
        assertTrue(koodistoBusinessService.hasAnyRelation("suhde502kanssa", "suhde502kanssa"));
    }
    
    @Test(expected = KoodistosAlreadyHaveSuhdeException.class)
    public void preventsAddingRelationThatReferencesKoodistoItselfMoreThanOnce() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.RINNASTEINEN);
        assertTrue(koodistoBusinessService.hasAnyRelation("suhde502kanssa", "suhde502kanssa"));
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.SISALTYY);
    }
    
    
    @Test
    public void onlyFetchesRelationsThatArePartOfTheLatestVersion() {
        assertEquals(1, koodistoBusinessService.getLatestKoodistoVersio("vaintuoreimmatrelaatiot").getAlakoodistos().size());
    }

    @Test
    public void willFetchRelationsForOlderVersions() {
        assertEquals(2, koodistoBusinessService.getKoodistoVersio("vaintuoreimmatrelaatiot", 1).getAlakoodistos().size());
    }
	@Test
    public void doesNotSetStartDateToNewDateWhenUpdatingKoodistoVersioThatIsInLuonnosTila() {
        KoodistoVersio kv = koodistoBusinessService.getLatestKoodistoVersio("http://koodisto15");
        Date startDate = kv.getVoimassaAlkuPvm();
        UpdateKoodistoDataType type = DataUtils.createUpdateKoodistoDataType(kv.getKoodisto().getKoodistoUri(), kv.getKoodisto().getOmistaja(), TilaType.LUONNOS,
                kv.getKoodisto().getOrganisaatioOid(), startDate, kv.getVoimassaLoppuPvm(), "NewName", kv.getVersio(), kv.getVersion());        
        koodistoBusinessService.updateKoodisto(type);
        assertEquals(startDate, koodistoBusinessService.getLatestKoodistoVersio("http://koodisto15").getVoimassaAlkuPvm());
    }

	@Test
    public void testSavingKoodisto() {
        String koodistoUri = "koodistonSaveTestiKoodisto0";
        int versio = 1;
        Tila tila = Tila.HYVAKSYTTY;
        Kieli kieli = Kieli.FI;
        String kuvaus = "UusiKuvaus";
        String nimi = "UusiNimi";
        List<RelationCodes> includesCodes = Arrays.asList(new RelationCodes("koodistonSaveTestiKoodisto1", 1));
        List<RelationCodes> withinCodes = Arrays.asList(new RelationCodes("koodistonSaveTestiKoodisto2", 1));
        List<RelationCodes> levelsWithCodes = Arrays.asList(new RelationCodes("koodistonSaveTestiKoodisto3", 1));
        KoodistoDto codesDTO = createKoodistoDto(koodistoUri, versio, tila, kieli, kuvaus, nimi, includesCodes, withinCodes, levelsWithCodes);

        KoodistoVersio result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        
        assertEquals(2, result.getAlakoodistos().size());
        assertEquals(2, result.getYlakoodistos().size());
        
        koodistoBusinessService.saveKoodisto(codesDTO);
        
        result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        
        assertEquals(2, result.getAlakoodistos().size());
        assertEquals(1, result.getYlakoodistos().size());
        
        assertEquals(koodistoUri, result.getKoodisto().getKoodistoUri());
        assertEquals(2, result.getVersio().intValue());
        assertEquals(Tila.LUONNOS, result.getTila());
        assertEquals(kieli, result.getMetadatas().get(0).getKieli());
        assertEquals(nimi, result.getMetadatas().get(0).getNimi());
        assertEquals(kuvaus, result.getMetadatas().get(0).getKuvaus());
    }

    private KoodistoDto createKoodistoDto(String koodistoUri, int versio, Tila tila, Kieli kieli, String kuvaus, String nimi, List<RelationCodes> includesCodes, List<RelationCodes> withinCodes, List<RelationCodes> levelsWithCodes) {
        KoodistoDto d = new KoodistoDto();
        
        d.setTila(tila);
        d.setVersio(versio);
        d.setVersion(0L);
        d.setKoodistoUri(koodistoUri);
        d.setOrganisaatioOid("1.2.2004.6");
        
        d.setPaivitysPvm(new Date());
        d.setVoimassaAlkuPvm(new Date());
        d.setVoimassaLoppuPvm(new Date());
        
        ArrayList<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();
        KoodistoMetadata md = new KoodistoMetadata();
        md.setKieli(kieli);
        md.setKuvaus(kuvaus);
        md.setNimi(nimi);
        metadata.add(md);
        d.setMetadata(metadata);
        
        d.setIncludesCodes(new ArrayList<RelationCodes>(includesCodes));
        d.setLevelsWithCodes(new ArrayList<RelationCodes>(levelsWithCodes));
        d.setWithinCodes(new ArrayList<RelationCodes>(withinCodes));
        
        return d;
    }
}