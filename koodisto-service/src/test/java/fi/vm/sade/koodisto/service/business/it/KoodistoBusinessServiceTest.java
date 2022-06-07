package fi.vm.sade.koodisto.service.business.it;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.repository.KoodistonSuhdeRepository;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.it.DataUtils;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.UpdateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

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
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest
@WithMockUser(value = "1.2.3.4.5", authorities = "APP_KOODISTO_CRUD_1.2.246.562.10.00000000001")
public class KoodistoBusinessServiceTest {

    private static final String CODES_WITH_RELATIONS = "809suhdetahan";

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodistonSuhdeRepository suhdeRepository;
    
    @Autowired
    private KoodistoConversionService conversionService;

    private static final Long KOODISTON_SUHDE = -6L;

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
        String koodistoUri = "www.kunnat.fi/kunta";

        List<KoodistoVersio> currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(1, currentVersions.size());
        KoodistoVersio newVersion = koodistoBusinessService.createNewVersion(koodistoUri).getData();
        assertNotSame(currentVersions.get(0).getVersio(), newVersion.getVersio());
        currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(2, currentVersions.size());
    }

    @Test
    public void testListAllVersions() {

        String uri = "www.kunnat.fi/kunta";

        List<KoodistoVersio> koodistos = listAllKoodistoVersions(uri);
        assertEquals(1, koodistos.size());
    }

    private KoodistoVersio createKoodisto(String omistaja, String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        List<KoodistoRyhma> ryhmas = koodistoBusinessService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType(
                omistaja, organisaatioOid, voimassaAlkuPvm, voimassaLoppuPvm, nimi);
        KoodistoVersio koodisto = koodistoBusinessService.createKoodisto(ryhmaUris, createKoodistoDataType);
        return koodisto;
    }

    @Test(expected = KoodiVersioHasRelationsException.class)
    public void testDeleteVersioWithKoodiVersioHasRelationsException() {
        final String koodistoUri = "testikoodisto.fi";
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

        final String koodistoUri = "koodisto20";
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
        koodistoBusinessService.removeRelation("koodisiirtyykoodisto", Arrays.asList("koodisto18"), SuhteenTyyppi.SISALTYY);
    }

    @Test
    public void removeRelation() {
        assertNotNull(suhdeRepository.findById(KOODISTON_SUHDE));
        koodistoBusinessService.removeRelation("suhde502kanssa", Arrays.asList("suhde501kanssa"), SuhteenTyyppi.SISALTYY);
        assertNull(suhdeRepository.findById(KOODISTON_SUHDE).orElse(null));
    }

    @Test
    public void addsRelation() {
        koodistoBusinessService.addRelation("koodisto20", "koodisto21", SuhteenTyyppi.RINNASTEINEN);
        assertTrue(koodistoBusinessService.hasAnyRelation("koodisto20", "koodisto21"));
    }

    @Test
    public void preventsAddingSameRelationMoreThanOnce() {
        KoodistoVersio latest = koodistoBusinessService.getLatestKoodistoVersio("suhde502kanssa");
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde501kanssa", SuhteenTyyppi.SISALTYY);
        assertEquals(1, suhdeRepository.countByYlakoodistoVersio(latest));
    }

    @Test
    public void preventsAddingSameRelationMoreThanOnceDespiteRelationType() {
        KoodistoVersio latest = koodistoBusinessService.getLatestKoodistoVersio("suhde502kanssa");
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde501kanssa", SuhteenTyyppi.RINNASTEINEN);
        assertEquals(1, suhdeRepository.countByYlakoodistoVersio(latest));
        assertEquals(0, suhdeRepository.countByAlakoodistoVersio(latest));
    }

    @Test(expected = KoodistoRelationToSelfException.class)
    public void addingRelationThatReferencesKoodistoItselfCausesError() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.SISALTYY);
        assertTrue(koodistoBusinessService.hasAnyRelation("suhde502kanssa", "suhde502kanssa"));
    }

    @Test(expected = KoodistoRelationToSelfException.class)
    public void preventsAddingRelationThatReferencesKoodistoItselfMultipleTimes() {
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.RINNASTEINEN);
        assertTrue(koodistoBusinessService.hasAnyRelation("suhde502kanssa", "suhde502kanssa"));
        koodistoBusinessService.addRelation("suhde502kanssa", "suhde502kanssa", SuhteenTyyppi.SISALTYY);
    }

    @Test
    public void willFetchRelationsForOlderVersions() {
        assertEquals(2, koodistoBusinessService.getKoodistoVersio("vaintuoreimmatrelaatiot", 1).getAlakoodistos().size());
    }
	@Test
    public void doesNotSetStartDateToNewDateWhenUpdatingKoodistoVersioThatIsInLuonnosTila() {
        KoodistoVersio kv = koodistoBusinessService.getLatestKoodistoVersio("koodisto15");
        Date startDate = kv.getVoimassaAlkuPvm();
        UpdateKoodistoDataType type = DataUtils.createUpdateKoodistoDataType(kv.getKoodisto().getKoodistoUri(), kv.getKoodisto().getOmistaja(), TilaType.LUONNOS,
                kv.getKoodisto().getOrganisaatioOid(), startDate, kv.getVoimassaLoppuPvm(), "NewName", kv.getVersio(), kv.getVersion());        
        koodistoBusinessService.updateKoodisto(type);
        assertEquals(startDate, koodistoBusinessService.getLatestKoodistoVersio("koodisto15").getVoimassaAlkuPvm());
    }

	@Test
	public void testSavingKoodisto() {
	    String koodistoUri = "koodistonSaveTestiKoodisto0";
	    int versio = 1;
	    List<RelationCodes> includesCodes = List.of(new RelationCodes("koodistonSaveTestiKoodisto1", 1, false, new HashMap<>(), new HashMap<>()));
	    List<RelationCodes> withinCodes = List.of(new RelationCodes("koodistonSaveTestiKoodisto2", 1, false, new HashMap<>(), new HashMap<>()));
	    List<RelationCodes> levelsWithCodes = List.of(new RelationCodes("koodistonSaveTestiKoodisto3", 1, false, new HashMap<>(), new HashMap<>()));
	    KoodistoDto codesDTO = createKoodistoDtoForSave(koodistoUri, versio, includesCodes, withinCodes, levelsWithCodes);

	    KoodistoVersio result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);

	    assertEquals(2, result.getAlakoodistos().size());
	    assertEquals(2, result.getYlakoodistos().size());

        koodistoBusinessService.saveKoodisto(codesDTO);
        result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);

	    assertEquals(2, result.getAlakoodistos().size());
	    assertEquals(1, result.getYlakoodistos().size());

	    assertEquals(codesDTO.getKoodistoUri(), result.getKoodisto().getKoodistoUri());
	    assertEquals(versio + 1, result.getVersio().intValue());
	    assertEquals(Tila.LUONNOS, result.getTila());
	    KoodistoMetadata expectedMeta = codesDTO.getMetadata().get(0);
	    assertEquals(expectedMeta.getKieli(), result.getMetadatas().iterator().next().getKieli());
	    assertEquals(expectedMeta.getNimi(), result.getMetadatas().iterator().next().getNimi());
	    assertEquals(expectedMeta.getKuvaus(), result.getMetadatas().iterator().next().getKuvaus());
	}

	@Test
	public void savesKoodistoWithoutCopyingPassiveRelations() {
	    String koodistoUri = "passiivisuhdeeikopioidu";
        KoodistoVersio result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        KoodistoDto dto = conversionService.convert(result, KoodistoDto.class);
        assertEquals(1, dto.getWithinCodes().size());
        dto.getMetadata().get(0).setNimi("Uusi");
        koodistoBusinessService.saveKoodisto(dto);
        result = koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
        assertTrue(result.getYlakoodistos().isEmpty());
    }

    @Test(expected=KoodistoTilaException.class)
    public void doesNotAllowTilaUpdateFromHyvaksyttyToLuonnos() {
        KoodistoVersio result = koodistoBusinessService.getLatestKoodistoVersio("koodisto12");
        KoodistoDto dto = conversionService.convert(result, KoodistoDto.class);
        dto.setTila(Tila.LUONNOS);
        koodistoBusinessService.saveKoodisto(dto);
    }

	@Test
	public void setsOldCodesAndCodeElementRelationsToPassiveWhenNewVersionIsCreated() throws Exception {
	    String koodistoUri = "vanhasuhdepassivoidaan";
	    koodistoBusinessService.createNewVersion(koodistoUri);
	    assertRelationsArePassive(koodistoBusinessService.getKoodistoVersio("vanhasuhdepassivoidaan", 2), false);
        SearchKoodisCriteriaType criteriaType = new SearchKoodisCriteriaType();
        criteriaType.setKoodiArvo("tamansuhdepassivoidaan");
        List<KoodiVersioWithKoodistoItem> koodiVersioWithKoodistoItems = koodiBusinessService.searchKoodis(criteriaType);
        assertEquals(koodiVersioWithKoodistoItems.size(), 1);
        KoodiVersio kv = koodiVersioWithKoodistoItems.get(0).getKoodiVersio();
        assertFalse(kv.getAlakoodis().isEmpty());
        for (KoodinSuhde ks : kv.getAlakoodis()) {
            assertCodeElementRelationIsPassive(false, ks);
        }
    }

	@Test
	public void setsLowerOldCodesAndCodeElementRelationsToPassiveWhenNewVersionIsCreated() throws Exception {
	    koodistoBusinessService.createNewVersion(CODES_WITH_RELATIONS);
	    Set<KoodistonSuhde> relations = koodistoBusinessService.getKoodistoVersio(CODES_WITH_RELATIONS, 1).getYlakoodistos();
	    assertEquals(2, relations.size());
	    for (KoodistonSuhde ks : relations) {
	        assertTrue(ks.isPassive());
	    }

        SearchKoodisCriteriaType criteriaType = new SearchKoodisCriteriaType();
        criteriaType.setKoodiArvo("sisaltyy706ja707");
        List<KoodiVersioWithKoodistoItem> koodiVersioWithKoodistoItems = koodiBusinessService.searchKoodis(criteriaType);
        assertEquals(koodiVersioWithKoodistoItems.size(), 1);
        KoodiVersio kv = koodiVersioWithKoodistoItems.get(0).getKoodiVersio();

	    Set<KoodinSuhde> codeRelations = kv.getYlakoodis();
	    // Passive relations don't follow to the new version (initially 2 relations)
	    assertEquals(1, codeRelations.size());
	    for (KoodinSuhde ks : codeRelations) {
            assertFalse(ks.isPassive());
        }
	}

    @Test
    public void setsRelationsToPassiveWhenKoodistoIsSetToPassive() throws Exception {
	    assertRelationsArePassive(givenKoodistoWithRelationsAndTila("vanhasuhdepassivoidaan", TilaType.PASSIIVINEN), false);
    }

	@Test
	public void activatesRelationsInPreviousVersion() throws Exception {
	    koodistoBusinessService.delete("vanhatsuhteetaktivoituupoistossa", 2);
	    KoodistoVersio latest = koodistoBusinessService.getLatestKoodistoVersio("vanhatsuhteetaktivoituupoistossa");
	    assertEquals(1, latest.getVersio().intValue());
	    assertRelationsAreActive(latest);
	    assertCodeElementRelationsAreActive(koodiBusinessService.getKoodi("wanhasuhdeaktivoituupoistossa").getKoodiVersios().iterator().next());
	}

	private void assertCodeElementRelationsAreActive(KoodiVersio koodiVersio) {
	    assertFalse(koodiVersio.getAlakoodis().isEmpty());
	    for (KoodinSuhde ks : koodiVersio.getAlakoodis()) {
	        assertFalse(ks.isPassive());
	    }
    }

    private KoodistoVersio givenKoodistoWithRelationsAndTila(String koodistoUri, TilaType tila) {
	    UpdateKoodistoDataType dataType = DataUtils.convert(koodistoBusinessService.getLatestKoodistoVersio(koodistoUri));
	    dataType.setTila(tila);
	    koodistoBusinessService.updateKoodisto(dataType);
	    return koodistoBusinessService.getLatestKoodistoVersio(koodistoUri);
	}
	
	private void assertRelationsAreActive(KoodistoVersio kv) {
	    assertFalse(kv.getAlakoodistos().isEmpty());
        for(KoodistonSuhde ks : kv.getAlakoodistos()) {
             assertFalse(ks.isPassive());
         }
    }

    private void assertRelationsArePassive(KoodistoVersio kv, boolean lowerPassive) {
        assertFalse(kv.getAlakoodistos().isEmpty());
        for(KoodistonSuhde ks : kv.getAlakoodistos()) {
             assertRelationIsPassive(lowerPassive, ks);
         }
    }

    private void assertRelationIsPassive(boolean lowerPassive, KoodistonSuhde ks) {
        assertTrue(ks.isPassive());
        assertTrue((!lowerPassive && ks.isYlaKoodistoPassive()) || lowerPassive && ! ks.isYlaKoodistoPassive());
        assertTrue((lowerPassive && ks.isAlaKoodistoPassive()) || !lowerPassive && !ks.isAlaKoodistoPassive());
    }

    private void assertCodeElementRelationIsPassive(boolean lowerPassive, KoodinSuhde ks) {
        assertTrue(ks.isPassive());
         assertTrue((!lowerPassive && ks.isYlaKoodiPassive()) || lowerPassive && ! ks.isYlaKoodiPassive());
         assertTrue((lowerPassive && ks.isAlaKoodiPassive()) || !lowerPassive && !ks.isAlaKoodiPassive());
    }

    private KoodistoDto createKoodistoDtoForSave(String koodistoUri, int versio, List<RelationCodes> includesCodes, List<RelationCodes> withinCodes, List<RelationCodes> levelsWithCodes) {
        KoodistoDto d = new KoodistoDto();
        d.setTila(Tila.HYVAKSYTTY);
        d.setVersio(versio);
        d.setVersion(0L);
        d.setKoodistoUri(koodistoUri);
        d.setOrganisaatioOid("1.2.2004.6");
        
        d.setPaivitysPvm(new Date());
        d.setVoimassaAlkuPvm(new Date());
        d.setVoimassaLoppuPvm(new Date());
        
        ArrayList<KoodistoMetadata> metadata = new ArrayList<KoodistoMetadata>();
        KoodistoMetadata md = new KoodistoMetadata();
        md.setKieli(Kieli.FI);
        md.setKuvaus("UusiKuvaus");
        md.setNimi("UusiNimi");
        metadata.add(md);
        d.setMetadata(metadata);
        
        d.setIncludesCodes(includesCodes);
        d.setLevelsWithCodes(levelsWithCodes);
        d.setWithinCodes(withinCodes);
        
        return d;
    }
}