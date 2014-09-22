package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.KoodistoChangesDto.SimpleCodesRelation;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:spring/simple-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoChangesServiceImplTest {
    
    private static final String KOODI_URI = "koivu";
    private final static String KOODISTO_URI = "lehtipuut";
    private final static String NAME = "Lehtipuut", DESCRIPTION = "pudottavat lehtensä syksyisin";
    private final static String NAME_EN = "Leaftrees", DESCRIPTION_EN = "leaves are dropped during autumn";
    private final static Date CURRENT_DATE = new Date();
    private static final Date FIRST_DATE = new Date(2000), SECOND_DATE = new Date(20050000), THIRD_DATE = new Date(30050000);
    
    private final static Integer VERSIO = 1;
    
    @ReplaceWithMock
    @Autowired
    private KoodistoBusinessService koodistoService;
    
    @ReplaceWithMock
    @Autowired
    private KoodiBusinessService koodiService;
    
    @Autowired
    private KoodistoChangesService service;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void returnsNoChangesIfNothingHasChanged() {
        assertResultIsNoChanges(givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersio(VERSIO)), VERSIO);
    }

    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        assertResultIsNoChanges(givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersio(VERSIO + 1)), VERSIO + 1);
    }
    
    @Test
    public void returnsHasChangedIfNameHasChanged() {
        String newName = KOODI_URI;
        assertResultHasMetadataChanges(givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersioWithMetadata(VERSIO + 1, newName, DtoFactory.KOODISTO_DESCRIPTION)), VERSIO + 1, new SimpleMetadataDto(newName, Kieli.FI, null));
    }
    
    @Test
    public void returnsHasChangedIfDescriptionHasChanged() {
        String newDescription = "parempi kuvaus";
        assertResultHasMetadataChanges(givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersioWithMetadata(VERSIO+1, DtoFactory.KOODISTO_NAME, newDescription)), VERSIO + 1, new SimpleMetadataDto(null, Kieli.FI, newDescription));
    }
    
    @Test
    public void returnsHasChangedIfMultipleMetadataHasChanged() {
        int versio = 6;
        String newDesc = "pudottaa lehdet syksyllä";
        String newNameEn = "drops leaves during autumn";
        KoodistoMetadata originalFi = givenKoodistoMetadata(NAME, DESCRIPTION, Kieli.FI);
        KoodistoMetadata originalEn = givenKoodistoMetadata(NAME_EN, DESCRIPTION_EN, Kieli.EN);
        KoodistoMetadata latestFi = givenKoodistoMetadata(NAME, newDesc, Kieli.FI);
        KoodistoMetadata latestEn = givenKoodistoMetadata(newNameEn, DESCRIPTION_EN, Kieli.EN);
        KoodistoVersio original = givenKoodistoVersioWithMetadata(versio, originalFi, originalEn);        
        KoodistoVersio latest = givenKoodistoVersioWithMetadata(versio + 1, latestFi, latestEn);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleMetadataDto(null, Kieli.FI, newDesc), 
                new SimpleMetadataDto(newNameEn, Kieli.EN, null));
    }
    
    @Test
    public void returnsHasChangedIfMetadataHasBeenRemoved() {
        int versio = 10;
        KoodistoVersio original = givenKoodistoVersioWithMetadata(versio, givenKoodistoMetadata(NAME_EN, DESCRIPTION_EN, Kieli.EN));
        KoodistoVersio latest = givenKoodistoVersioWithMetadata(versio + 1);
        KoodistoChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertTrue(result.poistuneetTiedot.contains(new SimpleMetadataDto(NAME_EN, Kieli.EN, DESCRIPTION_EN)));
    }
    
    @Test
    public void returnsHasChangedIfMetadataHasBeenAdded() {
        KoodistoVersio latest = givenKoodistoVersioWithMetadata(VERSIO + 1, NAME, DESCRIPTION);
        KoodistoVersio original = givenKoodistoVersio(VERSIO);
        assertResultHasMetadataChanges(givenResult(original, latest), VERSIO + 1, new SimpleMetadataDto(NAME, Kieli.FI, DESCRIPTION));
    }
    
    @Test
    public void metadataWithSameLanguageCannotBeBothInRemovedMetadatasAndInChangedMetadatas() {
        String newName = "tammi";
        KoodistoVersio latest = givenKoodistoVersioWithMetadata(VERSIO + 1, newName, DESCRIPTION);
        KoodistoVersio original = givenKoodistoVersioWithMetadata(VERSIO, NAME, DESCRIPTION);
        KoodistoChangesDto dto = givenResult(original, latest);
        assertTrue(dto.poistuneetTiedot.isEmpty());
        assertEquals(1, dto.muuttuneetTiedot.size());
        assertEquals(new SimpleMetadataDto(newName, Kieli.FI, null), dto.muuttuneetTiedot.get(0));
    }
    
    @Test
    public void returnsHasChangedIfStartDateHasChanged() {
        int versio = 4;
        KoodistoVersio original = givenKoodistoVersio(versio);
        KoodistoVersio latest = givenKoodistoVersioWithCustomDatesItIsInEffect(versio + 1, CURRENT_DATE, null);
        KoodistoChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(CURRENT_DATE, result.voimassaAlkuPvm);
    }

    @Test
    public void returnsHasChangedIfEndDateHasChanged() {
        int versio = 5;
        KoodistoVersio original = givenKoodistoVersio(versio);
        KoodistoVersio latest = givenKoodistoVersioWithCustomDatesItIsInEffect(versio + 1, original.getVoimassaAlkuPvm(), CURRENT_DATE);
        KoodistoChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(CURRENT_DATE, result.voimassaLoppuPvm);
        assertNull(result.poistettuVoimassaLoppuPvm);
    }
    
    @Test
    public void returnsHasChangedIfEndDateHasBeenRemoved() {
        int versio = 5;
        KoodistoVersio latest = givenKoodistoVersio(versio + 1);
        KoodistoVersio original = givenKoodistoVersioWithCustomDatesItIsInEffect(versio, CURRENT_DATE, CURRENT_DATE);
        KoodistoChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertNull(result.voimassaLoppuPvm);
        assertTrue(result.poistettuVoimassaLoppuPvm);
    }
    
    @Test
    public void returnsHasChangedIfTilaHasChanged() {
        KoodistoVersio latest = givenKoodistoVersio(VERSIO + 1);
        latest.setTila(Tila.PASSIIVINEN);
        KoodistoChangesDto result = givenResult(givenKoodistoVersio(VERSIO), latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(Tila.PASSIIVINEN, result.tila);
    }
    
    @Test
    public void usesLatestAcceptedVersionForComparison() {
        String changedSecondDescription = "jumbo";
        KoodistoVersio first = givenKoodistoVersioWithMetadata(VERSIO, givenKoodistoMetadata(NAME, DESCRIPTION, Kieli.FI));
        KoodistoVersio second = givenKoodistoVersioWithMetadata(VERSIO + 1, givenKoodistoMetadata(NAME, changedSecondDescription, Kieli.FI));
        KoodistoVersio third = givenKoodistoVersioWithTilaAndMetadata(VERSIO + 2 , Tila.LUONNOS, givenKoodistoMetadata(NAME, "huono kuvaus", Kieli.FI));
        assertResultWithTila(VERSIO + 1, changedSecondDescription, null, givenResultWithMultipleKoodistoVersios(VERSIO, true, first, second, third));
    }

    @Test
    public void doesNotUseLatestAcceptedVersionForComparison() {
        String changedThirdDescription = "huono kuvaus";
        KoodistoVersio first = givenKoodistoVersioWithMetadata(VERSIO, givenKoodistoMetadata(NAME, DESCRIPTION, Kieli.FI));
        KoodistoVersio second = givenKoodistoVersioWithMetadata(VERSIO + 1, givenKoodistoMetadata(NAME, "jumbo", Kieli.FI));
        KoodistoVersio third = givenKoodistoVersioWithTilaAndMetadata(VERSIO + 2 , Tila.LUONNOS, givenKoodistoMetadata(NAME, changedThirdDescription, Kieli.FI));
        assertResultWithTila(VERSIO + 2, changedThirdDescription, Tila.LUONNOS, givenResultWithMultipleKoodistoVersios(VERSIO, false, first, second, third));
    }
    
    @Test
    public void usesCodesVersionThatIsClosestToGivenDateForComparison() {
        assertGivenResultWithDateQuery(SECOND_DATE, false);
    }
    
    @Test
    public void usesFirstVersionForComparisonWhenDateUsedForQueryIsBeforeAnyVersion() {
        assertGivenResultWithDateQuery(new Date(0), true);
    }
    
    @Test
    public void returnsHasChangedIfRelationHaveBeenAdded() {
        Integer relationVersion = 3;
        String koodistoUri = "havupuut";
        KoodistoVersio original = givenKoodistoVersio(VERSIO);
        KoodistoVersio latest = givenKoodistoVersioWithRelations(VERSIO + 1, givenKoodistonSuhde(SuhteenTyyppi.RINNASTEINEN, null, givenKoodistoVersio(relationVersion, koodistoUri)));
        KoodistoChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.passivoidutKoodistonSuhteet.isEmpty());
        assertTrue(dto.poistetutKoodistonSuhteet.isEmpty());
        assertEquals(1, dto.lisatytKoodistonSuhteet.size());
        SimpleCodesRelation relation = dto.lisatytKoodistonSuhteet.get(0);
        assertEquals(koodistoUri, relation.koodistoUri);
        assertEquals(SuhteenTyyppi.RINNASTEINEN, relation.suhteenTyyppi);
        assertTrue(relation.lapsiKoodisto);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedIfRelationsHaveBeenRemoved() {
        Integer relationVersion = 3;
        String koodistoUri = "havupuut";
        KoodistoVersio latest = givenKoodistoVersio(VERSIO + 1);
        KoodistoVersio original = givenKoodistoVersioWithRelations(VERSIO, givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, givenKoodistoVersio(relationVersion, koodistoUri), null));
        KoodistoChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.passivoidutKoodistonSuhteet.isEmpty());
        assertTrue(dto.lisatytKoodistonSuhteet.isEmpty());
        assertEquals(1, dto.poistetutKoodistonSuhteet.size());
        SimpleCodesRelation relation = dto.poistetutKoodistonSuhteet.get(0);
        assertEquals(koodistoUri, relation.koodistoUri);
        assertEquals(SuhteenTyyppi.SISALTYY, relation.suhteenTyyppi);
        assertFalse(relation.lapsiKoodisto);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedIfRelationsHaveBeenTurnedIntoPassive() {
        Integer relationVersion = 3;
        String koodistoUri = "puut";
        KoodistoVersio related = givenKoodistoVersio(relationVersion, koodistoUri);
        KoodistoVersio original = givenKoodistoVersioWithRelations(VERSIO, givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, related));
        KoodistoVersio latest = givenKoodistoVersioWithRelations(VERSIO + 1, givenPassiveKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, related, true, false));
        KoodistoChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.lisatytKoodistonSuhteet.isEmpty());
        assertTrue(dto.poistetutKoodistonSuhteet.isEmpty());
        assertEquals(1, dto.passivoidutKoodistonSuhteet.size());
        SimpleCodesRelation relation = dto.passivoidutKoodistonSuhteet.get(0);
        assertEquals(koodistoUri, relation.koodistoUri);
        assertEquals(SuhteenTyyppi.SISALTYY, relation.suhteenTyyppi);
        assertTrue(relation.lapsiKoodisto);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedForMultipleRelations() {
        Integer versio = 1;
        KoodistoVersio original = givenKoodistoVersioWithRelations(versio, givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodistoVersio(3, "juuret")), givenKoodistonSuhde(SuhteenTyyppi.RINNASTEINEN, null, givenKoodistoVersio(3, "lehdet")));
        KoodistoVersio latest = givenKoodistoVersioWithRelations(versio + 1, givenPassiveKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodistoVersio(3, "juuret"), true, false), givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodistoVersio(3, "oksat")));
        KoodistoChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals(1, dto.lisatytKoodistonSuhteet.size());
        assertEquals(1, dto.poistetutKoodistonSuhteet.size());
        assertEquals(1, dto.passivoidutKoodistonSuhteet.size());
    }
    
    @Test
    public void returnsHasChangedForMultipleRelationsAndTreatsRelationsWithSameCodeUriButDifferentRelationTypeAsDifferent() {
        KoodistoVersio relationCode = givenKoodistoVersio(3, "havupuut");
        KoodistoVersio original = givenKoodistoVersioWithRelations(VERSIO, givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodistoVersio(3, "puut")), givenKoodistonSuhde(SuhteenTyyppi.RINNASTEINEN, null, relationCode));
        KoodistoVersio latest = givenKoodistoVersioWithRelations(VERSIO + 1, givenPassiveKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodistoVersio(3, "puut"), true, false), givenKoodistonSuhde(SuhteenTyyppi.SISALTYY, null, relationCode));
        KoodistoChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals(1, dto.lisatytKoodistonSuhteet.size());
        assertEquals(1, dto.poistetutKoodistonSuhteet.size());
        assertEquals(1, dto.passivoidutKoodistonSuhteet.size());
    }
    
    @Test
    public void returnsHasChangedWhenCodeElementHasBeenAdded() {
        KoodistoChangesDto result = givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersioWithKoodiVersio(VERSIO + 1));
        assertResultWithKoodiChanges(VERSIO + 1, result, 1, 0, 0);
    }
    
    @Test
    public void returnsHasChangedWhenCodeElementHasBeenChanged() {
        
    }
    
    @Test
    public void returnsHasChangedWhenCodeElementHasBeenRemoved() {
        //KoodistoChangesDto result = givenResult(givenKoodistoVersioWithKoodiVersio(VERSIO), givenKoodistoVersio(VERSIO + 1));
        //assertResultWithKoodiChanges(VERSIO + 1, result, 0, 0, 1);
    }
    
    @Test
    public void returnsHasChangedWhenCodeElementsHaveBeenAddedAndRemoved() {
    }
    
    private void assertGivenResultWithDateQuery(Date query, boolean shouldUseFirst) {
        String descriptionChangedForSecond = "kuvausta norsusta";
        String nameChangedForThird = "Otus";
        KoodistoVersio first = givenKoodistoVersioWithMetaDataAndCustomDateItWasCreated(VERSIO, FIRST_DATE, givenKoodistoMetadata(NAME, DESCRIPTION, Kieli.FI));
        KoodistoVersio second = givenKoodistoVersioWithMetaDataAndCustomDateItWasCreated(VERSIO + 1, SECOND_DATE, givenKoodistoMetadata(NAME, descriptionChangedForSecond, Kieli.FI));
        KoodistoVersio third = givenKoodistoVersioWithMetaDataAndCustomDateItWasCreated(VERSIO + 2, THIRD_DATE, givenKoodistoMetadata(nameChangedForThird, descriptionChangedForSecond, Kieli.FI));
        KoodistoChangesDto dto = givenResultWithMultipleKoodistoVersiosForDateQuery(query, false, first, second, third);
        assertEquals(3, dto.viimeisinVersio.intValue());
        SimpleMetadataDto data = dto.muuttuneetTiedot.get(0);
        assertEquals(nameChangedForThird, data.nimi);
        if (shouldUseFirst) {
            assertEquals(descriptionChangedForSecond, data.kuvaus);
        } else {
            assertNull(data.kuvaus);
        }
    }
    
    private void assertResultWithKoodiChanges(Integer expectedVersio, KoodistoChangesDto result, int expectedAmountAdded, int expectedAmountChanged, int expectedAmountDeleted) {
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(expectedAmountAdded, result.lisatytKoodit.size());
        assertEquals(expectedAmountDeleted, result.poistetutKoodit.size());
        assertEquals(expectedAmountChanged, result.muuttuneetKoodit.size());
    }

    private void assertResultWithTila(Integer expectedVersio, String expectedDescription, Tila expectedTila, KoodistoChangesDto result) {
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(expectedVersio, result.viimeisinVersio);
        assertEquals(expectedTila, result.tila);
        assertEquals(expectedDescription, result.muuttuneetTiedot.get(0).kuvaus);
    }

    private void assertResultIsNoChanges(KoodistoChangesDto result, int versio) {
        assertEquals(MuutosTila.EI_MUUTOKSIA, result.muutosTila);
        assertEquals(versio, result.viimeisinVersio.intValue());
        assertTrue(result.muuttuneetTiedot.isEmpty());
        assertNotNull(result.viimeksiPaivitetty);
        assertNull(result.voimassaAlkuPvm);
        assertNull(result.voimassaLoppuPvm);
        assertNull(result.tila);
    }
    
    private void assertResultHasMetadataChanges(KoodistoChangesDto result, int versio, SimpleMetadataDto ... expecteds) {
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertTrue(result.muuttuneetTiedot.containsAll(Arrays.asList(expecteds)));
        assertEquals(versio, result.viimeisinVersio.intValue());
    }
    
    private KoodistoChangesDto givenResultWithMultipleKoodistoVersiosForDateQuery(Date date, boolean compareToLatestAccepted, KoodistoVersio ... versios) {
        if (!compareToLatestAccepted) {
            returnLatestKoodistoVersioFromMockedKoodistoService(versios);
        }
        returnGivenKoodistoVersiosWithKoodistoFromMockedKoodistoService(versios);
        return service.getChangesDto(KOODISTO_URI, new DateTime(date), compareToLatestAccepted);
    }
    
    private KoodistoChangesDto givenResult(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        Integer versio = koodistoVersio.getVersio();
        when(koodistoService.getKoodistoVersio(KOODISTO_URI, versio)).thenReturn(koodistoVersio);
        when(koodistoService.getLatestKoodistoVersio(KOODISTO_URI)).thenReturn(latest);
        return service.getChangesDto(KOODISTO_URI, versio, false);
    }
    
    private KoodistoChangesDto givenResultWithMultipleKoodistoVersios(Integer versio, boolean compareToLatestAccepted, KoodistoVersio ... versios) {
        when(koodistoService.getKoodistoVersio(KOODISTO_URI, versio)).thenReturn(getMatchingCodesVersion(versio, versios));
        if (compareToLatestAccepted) {
            returnGivenKoodistoVersiosWithKoodistoFromMockedKoodistoService(versios);
        } else {
            returnLatestKoodistoVersioFromMockedKoodistoService(versios);
        }
        return service.getChangesDto(KOODISTO_URI, versio, compareToLatestAccepted);
    }
    
    private void returnLatestKoodistoVersioFromMockedKoodistoService(KoodistoVersio[] versios) {
        KoodistoVersio latest = null;
        for (KoodistoVersio kv : versios) {
            latest = latest == null || kv.getVersio() > latest.getVersio() ? kv : latest;
        }
        when(koodistoService.getLatestKoodistoVersio(KOODISTO_URI)).thenReturn(latest);        
    }

    private void returnGivenKoodistoVersiosWithKoodistoFromMockedKoodistoService(KoodistoVersio[] versios) {
        Koodisto koodisto = Mockito.mock(Koodisto.class);
        when(koodisto.getKoodistoVersios()).thenReturn(new HashSet<KoodistoVersio>(Arrays.asList(versios)));
        when(koodistoService.getKoodistoByKoodistoUri(KOODISTO_URI)).thenReturn(koodisto);
    }

    private KoodistoVersio getMatchingCodesVersion(Integer versio, KoodistoVersio ... versios) {
        for (KoodistoVersio kv : versios) {
            if (kv.getVersio().equals(versio)) {
                return kv;
            }
        }
        throw new IllegalArgumentException("Could not find koodistoversio using versio: " + versio);
    }

    private KoodistoVersio givenKoodistoVersio(Integer versio) {
        return givenKoodistoVersio(versio, KOODISTO_URI);
    }
    
    private KoodistoVersio givenKoodistoVersio(Integer versio, String koodistoUri) {
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri(koodistoUri);
        return DtoFactory.createKoodistoVersio(koodisto, versio).build();
    }
    
    private KoodistoVersio givenKoodistoVersioWithMetadata(Integer versio, String name, String description) {
        return DtoFactory.createKoodistoVersioWithMetadata(new Koodisto(), versio, name, description, Kieli.FI).build();
    }
    
    private KoodistoVersio givenKoodistoVersioWithMetadata(Integer versio, KoodistoMetadata ... datas) {
        return DtoFactory.createKoodistoVersioWithMetadata(new Koodisto(), versio, datas).build();
    }
    
    private KoodistoVersio givenKoodistoVersioWithCustomDatesItIsInEffect(int versio, Date startDate, Date endDate) {
        return DtoFactory.createKoodistoVersioWithStartAndEndDates(new Koodisto(), versio, startDate, endDate).build();
    }
    
    private KoodistoMetadata givenKoodistoMetadata(String name, String description, Kieli kieli) {
        KoodistoMetadata meta = new KoodistoMetadata();
        meta.setNimi(name);
        meta.setKuvaus(description);
        meta.setKieli(kieli);
        return meta;
    }
    
    private KoodistoVersio givenKoodistoVersioWithTilaAndMetadata(int versio, Tila tila, KoodistoMetadata ...koodistoMetadatas) {
        KoodistoVersio kv = givenKoodistoVersioWithMetadata(versio,  koodistoMetadatas);
        kv.setTila(tila);
        return kv;
    }
    
    private KoodistoVersio givenKoodistoVersioWithMetaDataAndCustomDateItWasCreated(int versio, Date createdDate, KoodistoMetadata ... koodistoMetadata) {
        KoodistoVersio kv = givenKoodistoVersioWithMetadata(versio, koodistoMetadata);
        kv.setLuotu(createdDate);
        return kv;
    }
    
    private KoodistoVersio givenKoodistoVersioWithRelations(int versio, KoodistonSuhde ... relations) {
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri(KOODISTO_URI);
        return DtoFactory.createKoodistoVersioWithRelations(koodisto, versio, relations).build();
    }
    
    private KoodistonSuhde givenKoodistonSuhde(SuhteenTyyppi tyyppi, KoodistoVersio parent, KoodistoVersio child) {
        return givenPassiveKoodistonSuhde(tyyppi, parent, child, false, false);
    }
    
    private KoodistonSuhde givenPassiveKoodistonSuhde(SuhteenTyyppi tyyppi, KoodistoVersio parent, KoodistoVersio child, boolean parentPassive, boolean childPassive) {
        return DtoFactory.createKoodistonSuhde(tyyppi, child, parent, parentPassive, childPassive).build();
    }
    
    private KoodistoVersio givenKoodistoVersioWithKoodiVersio(int versio) {
        return givenKoodistoVersioWithKoodiVersios(versio, DtoFactory.createKoodiVersioWithUriAndVersio(KOODI_URI, VERSIO).build());
    }
    
    private KoodistoVersio givenKoodistoVersioWithKoodiVersios(int versio, KoodiVersio ... koodiVersios) {
        return DtoFactory.createKoodistoVersioWithKoodiVersios(versio, KOODISTO_URI, koodiVersios).build();
    }
}
