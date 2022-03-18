package fi.vm.sade.koodisto.service.business.changes.impl;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.SimpleCodeElementRelation;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.ChangesService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import fi.vm.sade.koodisto.test.support.builder.KoodiVersioBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KoodiChangesServiceImplTest {
    
    private static final Date CURRENT_DATE = new Date();
    
    private static final Date FIRST_DATE = new Date(1000), SECOND_DATE = new Date(20000000), THIRD_DATE = new Date(30000000);
    
    private static final String KOODI_URI = "elefantti";
    
    private static final String NAME = "elefantti", SHORT_NAME = "fantti", DESCRIPTION = "kärsäeläin";
    private static final String NAME_EN = "African elephant", SHORT_NAME_EN = "elephant", DESCRIPTION_EN = "trunkard";
    private static final String NAME_SV = "elefant", SHORT_NAME_SV = "kort", DESCRIPTION_SV = "stora flockdjur";
    private static final Integer VERSIO = 1;

    @MockBean// TODO was replacewithmock toimiiko oikein?
    private KoodiBusinessService koodiService;
    
    @MockBean
    private KoodistoBusinessService koodistoService;
    
    @Autowired
    private KoodiChangesService service;
    
    /*@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }*/
        
    @Test
    public void returnsNoChangesIfNothingHasChanged() {
        assertResultIsNoChanges(givenResult(givenKoodiVersio(VERSIO), givenKoodiVersio(VERSIO)), VERSIO);
    }

    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        assertResultIsNoChanges(givenResult(givenKoodiVersio(VERSIO), givenKoodiVersio(VERSIO + 1)), VERSIO + 1);
    }
    
    @Test
    public void returnsHasChangedIfNameHasChanged() {
        int versio = 3;
        String newName = "norsu";
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio +1, newName, SHORT_NAME, DESCRIPTION, Kieli.FI);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleKoodiMetadataDto(newName, Kieli.FI, null, null));
    }
    
    @Test
    public void returnsHasChangedIfShortNameHasChanged() {
        int versio = 10;
        String newShort = "norsu";
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio +1, NAME, newShort, DESCRIPTION, Kieli.FI);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleKoodiMetadataDto(null, Kieli.FI, null, newShort));
    }
    
    @Test
    public void returnsHasChangedIfDescriptionHasChanged() {
        int versio = 6;
        String newDesc = "isoin maalla liikkuva vegetaristi";
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio +1, NAME, SHORT_NAME, newDesc, Kieli.FI);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleKoodiMetadataDto(null, Kieli.FI, newDesc, null));
    }
    
    @Test
    public void returnsHasChangedIfMultipleMetadataHasChanged() {
        int versio = 6;
        String newDesc = "isoin maalla liikkuva vegetaristi";
        String newNameEn = "Asian elephant";
        String newDescSv = "storste flockdjur";
        KoodiMetadata originalFi = givenKoodiMetadata(NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiMetadata originalEn = givenKoodiMetadata(NAME_EN, SHORT_NAME_EN, DESCRIPTION_EN, Kieli.EN);
        KoodiMetadata originalSv = givenKoodiMetadata(NAME_SV, SHORT_NAME_SV, DESCRIPTION_SV, Kieli.SV);
        KoodiMetadata latestFi = givenKoodiMetadata(NAME, SHORT_NAME, newDesc, Kieli.FI);
        KoodiMetadata latestEn = givenKoodiMetadata(newNameEn, SHORT_NAME_EN, DESCRIPTION_EN, Kieli.EN);
        KoodiMetadata latestSv = givenKoodiMetadata(NAME_SV, SHORT_NAME_SV, newDescSv, Kieli.SV);
        KoodiVersio original = givenKoodiVersioWithMetadata(versio, originalFi, originalEn, originalSv);        
        KoodiVersio latest = givenKoodiVersioWithMetadata(versio + 1, latestFi, latestEn, latestSv);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleKoodiMetadataDto(null, Kieli.FI, newDesc, null), 
                new SimpleKoodiMetadataDto(newNameEn, Kieli.EN, null, null), new SimpleKoodiMetadataDto(null, Kieli.SV, newDescSv, null));
    }
    
    @Test
    public void returnsHasChangedIfMetadataHasBeenRemoved() {
        int versio = 10;
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio latest = givenKoodiVersio(versio +1);
        KoodiChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertTrue(result.poistuneetTiedot.contains(new SimpleKoodiMetadataDto(NAME, Kieli.FI, DESCRIPTION, SHORT_NAME)));
    }
    
    @Test
    public void returnsHasChangedIfMetadataHasBeenAdded() {
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(VERSIO + 1, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio original = givenKoodiVersio(VERSIO);
        assertResultHasMetadataChanges(givenResult(original, latest), VERSIO + 1, new SimpleKoodiMetadataDto(NAME, Kieli.FI, DESCRIPTION, SHORT_NAME));
    }
    
    @Test
    public void metadataWhereDescriptionAndShortNameIsRemovedIsShownAsRemoved() {
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(VERSIO + 1, NAME, null, null, Kieli.FI);
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(VERSIO, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);;
        assertResultHasMetadataChanges(givenResult(original, latest), VERSIO + 1, new SimpleKoodiMetadataDto(null, Kieli.FI, ChangesService.REMOVED_METADATA_FIELD, ChangesService.REMOVED_METADATA_FIELD));
    }
    
    @Test
    public void metadataWithSameLanguageCannotBeBothInRemovedMetadatasAndInChangedMetadatas() {
        String newName = "norsu";
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(VERSIO + 1, newName, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(VERSIO, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiChangesDto dto = givenResult(original, latest);
        assertTrue(dto.poistuneetTiedot.isEmpty());
        assertEquals(1, dto.muuttuneetTiedot.size());
        assertEquals(new SimpleKoodiMetadataDto(newName, Kieli.FI, null, null), dto.muuttuneetTiedot.get(0));
    }
    
    @Test
    public void returnsUpdateDateEvenWithNoChanges() {
        int versio = 2;
        assertNotNull(givenResult(givenKoodiVersio(versio), givenKoodiVersio(versio)).viimeksiPaivitetty);
    }
    
    @Test
    public void returnsUpdateDateEvenWithChanges() {
        int versio = 3;
        KoodiVersio original = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio +1, "norsu", SHORT_NAME, DESCRIPTION, Kieli.FI);
        assertNotNull(givenResult(original, latest).viimeksiPaivitetty);
    }
    
    @Test
    public void returnsHasChangedIfStartDateHasChanged() {
        int versio = 4;
        KoodiVersio original = givenKoodiVersio(versio);
        KoodiVersio latest = givenKoodiVersioWithCustomDatesItIsInEffect(versio + 1, CURRENT_DATE, null);
        KoodiChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(CURRENT_DATE, result.voimassaAlkuPvm);
    }

    @Test
    public void returnsHasChangedIfEndDateHasChanged() {
        int versio = 5;
        KoodiVersio original = givenKoodiVersio(versio);
        KoodiVersio latest = givenKoodiVersioWithCustomDatesItIsInEffect(versio + 1, original.getVoimassaAlkuPvm(), CURRENT_DATE);
        KoodiChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(CURRENT_DATE, result.voimassaLoppuPvm);
        assertNull(result.poistettuVoimassaLoppuPvm);
    }
    
    @Test
    public void returnsHasChangedIfEndDateHasBeenRemoved() {
        int versio = 5;
        KoodiVersio latest = givenKoodiVersio(versio + 1);
        KoodiVersio original = givenKoodiVersioWithCustomDatesItIsInEffect(versio, CURRENT_DATE, CURRENT_DATE);
        KoodiChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertNull(result.voimassaLoppuPvm);
        assertTrue(result.poistettuVoimassaLoppuPvm);
    }
    
    @Test
    public void returnsHasChangedIfTilaHasChanged() {
        KoodiVersio latest = givenKoodiVersio(VERSIO + 1);
        latest.setTila(Tila.PASSIIVINEN);
        KoodiChangesDto result = givenResult(givenKoodiVersio(VERSIO), latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(Tila.PASSIIVINEN, result.tila);
    }
    
    @Test
    public void usesLatestAcceptedVersionForComparison() {
        String changedSecondDescription = "jumbo";
        KoodiVersio first = givenKoodiVersioWithMetadata(VERSIO, givenKoodiMetadata(NAME, SHORT_NAME, DESCRIPTION, Kieli.FI));
        KoodiVersio second = givenKoodiVersioWithMetadata(VERSIO + 1, givenKoodiMetadata(NAME, SHORT_NAME, changedSecondDescription, Kieli.FI));
        KoodiVersio third = givenKoodiVersioWithTilaAndMetadata(VERSIO + 2 , Tila.LUONNOS, givenKoodiMetadata(NAME, SHORT_NAME, "huono kuvaus", Kieli.FI));
        assertResultWithTila(VERSIO + 1, changedSecondDescription, null, givenResultWithMultipleKoodiVersios(VERSIO, true, first, second, third));
    }
    
    @Test
    public void doesNotCompareLatestVersionAgainstLatestAcceptedVersion() {
        KoodiChangesDto result = givenResultWithMultipleKoodiVersios(VERSIO + 2, true, 
                givenKoodiVersioWithMetadata(VERSIO, givenKoodiMetadata(NAME, SHORT_NAME, DESCRIPTION, Kieli.FI)), 
                givenKoodiVersioWithMetadata(VERSIO + 1, givenKoodiMetadata(NAME, SHORT_NAME, "jumbo", Kieli.FI)), 
                givenKoodiVersioWithTilaAndMetadata(VERSIO + 2 , Tila.LUONNOS, givenKoodiMetadata(NAME, SHORT_NAME, "huono kuvaus", Kieli.FI)));
        assertResultIsNoChanges(result, 2);
    }

    @Test
    public void doesNotUseLatestAcceptedVersionForComparison() {
        String changedThirdDescription = "huono kuvaus";
        KoodiVersio first = givenKoodiVersioWithMetadata(VERSIO, givenKoodiMetadata(NAME, SHORT_NAME, DESCRIPTION, Kieli.FI));
        KoodiVersio second = givenKoodiVersioWithMetadata(VERSIO + 1, givenKoodiMetadata(NAME, SHORT_NAME, "jumbo", Kieli.FI));
        KoodiVersio third = givenKoodiVersioWithTilaAndMetadata(VERSIO + 2 , Tila.LUONNOS, givenKoodiMetadata(NAME, SHORT_NAME, changedThirdDescription, Kieli.FI));
        assertResultWithTila(VERSIO + 2, changedThirdDescription, Tila.LUONNOS, givenResultWithMultipleKoodiVersios(VERSIO, false, first, second, third));
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
        String koodiUri = "kirahvi";
        KoodiVersio original = givenKoodiVersio(VERSIO);
        KoodiVersio latest = givenKoodiVersioWithRelations(VERSIO + 1, givenKoodinSuhde(SuhteenTyyppi.RINNASTEINEN, null, givenKoodiVersio(relationVersion, koodiUri)));
        KoodiChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.passivoidutKoodinSuhteet.isEmpty());
        assertTrue(dto.poistetutKoodinSuhteet.isEmpty());
        assertEquals(1, dto.lisatytKoodinSuhteet.size());
        SimpleCodeElementRelation relation = dto.lisatytKoodinSuhteet.get(0);
        assertEquals(koodiUri, relation.koodiUri);
        assertEquals(SuhteenTyyppi.RINNASTEINEN, relation.suhteenTyyppi);
        assertTrue(relation.lapsiKoodi);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedIfRelationsHaveBeenRemoved() {
        Integer relationVersion = 3;
        String koodiUri = "kirahvi";
        KoodiVersio latest = givenKoodiVersio(VERSIO + 1);
        KoodiVersio original = givenKoodiVersioWithRelations(VERSIO, givenKoodinSuhde(SuhteenTyyppi.SISALTYY, givenKoodiVersio(relationVersion, koodiUri), null));
        KoodiChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.passivoidutKoodinSuhteet.isEmpty());
        assertTrue(dto.lisatytKoodinSuhteet.isEmpty());
        assertEquals(1, dto.poistetutKoodinSuhteet.size());
        SimpleCodeElementRelation relation = dto.poistetutKoodinSuhteet.get(0);
        assertEquals(koodiUri, relation.koodiUri);
        assertEquals(SuhteenTyyppi.SISALTYY, relation.suhteenTyyppi);
        assertFalse(relation.lapsiKoodi);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedIfRelationsHaveBeenTurnedIntoPassive() {
        Integer relationVersion = 3;
        String koodiUri = "kirahvi";
        KoodiVersio related = givenKoodiVersio(relationVersion, koodiUri);
        KoodiVersio original = givenKoodiVersioWithRelations(VERSIO, givenKoodinSuhde(SuhteenTyyppi.SISALTYY, null, related));
        KoodiVersio latest = givenKoodiVersioWithRelations(VERSIO + 1, givenPassiveKoodinSuhde(SuhteenTyyppi.SISALTYY, null, related, true, false));
        KoodiChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.lisatytKoodinSuhteet.isEmpty());
        assertTrue(dto.poistetutKoodinSuhteet.isEmpty());
        assertEquals(1, dto.passivoidutKoodinSuhteet.size());
        SimpleCodeElementRelation relation = dto.passivoidutKoodinSuhteet.get(0);
        assertEquals(koodiUri, relation.koodiUri);
        assertEquals(SuhteenTyyppi.SISALTYY, relation.suhteenTyyppi);
        assertTrue(relation.lapsiKoodi);
        assertEquals(relationVersion, relation.versio);
    }
    
    @Test
    public void returnsHasChangedForMultipleRelations() {
        KoodiVersio original = givenKoodiVersioWithRelations(VERSIO, givenKoodinSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodiVersio(3, "kirahvi")), givenKoodinSuhde(SuhteenTyyppi.RINNASTEINEN, null, givenKoodiVersio(3, "seepra")));
        KoodiVersio latest = givenKoodiVersioWithRelations(VERSIO + 1, givenPassiveKoodinSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodiVersio(3, "kirahvi"), true, false), givenKoodinSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodiVersio(3, "gnu")));
        KoodiChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals(1, dto.lisatytKoodinSuhteet.size());
        assertEquals(1, dto.poistetutKoodinSuhteet.size());
        assertEquals(1, dto.passivoidutKoodinSuhteet.size());
    }
    
    @Test
    public void returnsHasChangedForMultipleRelationsAndTreatsRelationsWithSameCodeUriButDifferentRelationTypeAsDifferent() {
        KoodiVersio relationCode = givenKoodiVersio(3, "seepra");
        KoodiVersio original = givenKoodiVersioWithRelations(VERSIO, givenKoodinSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodiVersio(3, "kirahvi")), givenKoodinSuhde(SuhteenTyyppi.RINNASTEINEN, null, relationCode));
        KoodiVersio latest = givenKoodiVersioWithRelations(VERSIO + 1, givenPassiveKoodinSuhde(SuhteenTyyppi.SISALTYY, null, givenKoodiVersio(3, "kirahvi"), true, false), givenKoodinSuhde(SuhteenTyyppi.SISALTYY, null, relationCode));
        KoodiChangesDto dto = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals(1, dto.lisatytKoodinSuhteet.size());
        assertEquals(1, dto.poistetutKoodinSuhteet.size());
        assertEquals(1, dto.passivoidutKoodinSuhteet.size());
    }
    
    @Test
    public void returnsHasBeenDeletedIfCodeElementIsNotFoundInLatestCodes() {
        assertResultIsDeleted(givenDeletedResult(givenKoodiVersio(VERSIO), givenKoodiVersio(VERSIO + 1), false));
    }
    
    @Test
    public void returnsHasBeenDeletedIfCodeElementIsNotFoundInLatestAcceptedCodes() {
        assertResultIsDeleted(givenDeletedResult(givenKoodiVersio(VERSIO), givenKoodiVersio(VERSIO + 1), true));
    }
    
    private void assertGivenResultWithDateQuery(Date query, boolean shouldUseFirst) {
        String descriptionChangedForSecond = "kuvausta norsusta";
        String nameChangedForThird = "Otus";
        KoodiVersio first = givenKoodiVersioWithMetaDataAndCustomDateItWasCreated(VERSIO, FIRST_DATE, givenKoodiMetadata(NAME, SHORT_NAME, DESCRIPTION, Kieli.FI));
        KoodiVersio second = givenKoodiVersioWithMetaDataAndCustomDateItWasCreated(VERSIO + 1, SECOND_DATE, givenKoodiMetadata(NAME, SHORT_NAME, descriptionChangedForSecond, Kieli.FI));
        KoodiVersio third = givenKoodiVersioWithMetaDataAndCustomDateItWasCreated(VERSIO + 2, THIRD_DATE, givenKoodiMetadata(nameChangedForThird, SHORT_NAME, descriptionChangedForSecond, Kieli.FI));
        KoodiChangesDto dto = givenResultWithMultipleKoodiVersiosForDateQuery(query, false, first, second, third);
        assertEquals(3, dto.viimeisinVersio.intValue());
        SimpleKoodiMetadataDto data = dto.muuttuneetTiedot.get(0);
        assertEquals(nameChangedForThird, data.nimi);
        if (shouldUseFirst) {
            assertEquals(descriptionChangedForSecond, data.kuvaus);
        } else {
            assertNull(data.kuvaus);
        }
    }
    
    private void assertResultWithTila(int expectedVersion, String expectedDescription, Tila expectedTila, KoodiChangesDto result) {
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(expectedTila, result.tila);
        assertEquals(expectedVersion, result.viimeisinVersio.intValue());
        assertEquals(1, result.muuttuneetTiedot.size());
        assertEquals(expectedDescription, result.muuttuneetTiedot.get(0).kuvaus);
    }
    
    private void assertResultIsNoChanges(KoodiChangesDto result, int versio) {
        assertEquals(KOODI_URI, result.koodiUri);
        assertEquals(MuutosTila.EI_MUUTOKSIA, result.muutosTila);
        assertEquals(versio, result.viimeisinVersio.intValue());
        assertTrue(result.lisatytKoodinSuhteet.isEmpty());
        assertTrue(result.poistetutKoodinSuhteet.isEmpty());
        assertTrue(result.muuttuneetTiedot.isEmpty());
        assertNotNull(result.viimeksiPaivitetty);
        assertNull(result.voimassaAlkuPvm);
        assertNull(result.voimassaLoppuPvm);
        assertNull(result.tila);
    }
    
    private void assertResultIsDeleted(KoodiChangesDto result) {
        assertEquals(KOODI_URI, result.koodiUri);
        assertEquals(MuutosTila.POISTETTU, result.muutosTila);
    }
    
    private void assertResultHasMetadataChanges(KoodiChangesDto result, int versio, SimpleKoodiMetadataDto ... expecteds) {
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertTrue(result.muuttuneetTiedot.containsAll(Arrays.asList(expecteds)));
        assertEquals(versio, result.viimeisinVersio.intValue());
    }
    
    private KoodiChangesDto givenResult(KoodiVersio koodiVersio, KoodiVersio latest) {
        Integer versio = koodiVersio.getVersio();
        when(koodiService.getKoodiVersio(KOODI_URI, versio)).thenReturn(koodiVersio);
        when(koodiService.getLatestKoodiVersio(KOODI_URI)).thenReturn(latest);
        when(koodistoService.getKoodistoByKoodistoUri(any())).thenReturn(latest.getKoodi().getKoodisto());
        return service.getChangesDto(KOODI_URI, versio, false);
    }
    
    private KoodiChangesDto givenDeletedResult(KoodiVersio koodiVersio, KoodiVersio latest, boolean compareToLatestAccepted) {
        Integer versio = koodiVersio.getVersio();
        when(koodiService.getKoodiVersio(KOODI_URI, versio)).thenReturn(koodiVersio);
        when(koodiService.getLatestKoodiVersio(KOODI_URI)).thenReturn(latest);
        when(koodistoService.getKoodistoByKoodistoUri(any())).thenReturn(DtoFactory.createKoodistoVersio(new Koodisto(), 999).build().getKoodisto());
        return service.getChangesDto(KOODI_URI, versio, false);
    }
    
    private KoodiChangesDto givenResultWithMultipleKoodiVersios(Integer versio, boolean compareToLatestAccepted, KoodiVersio ... versios) {
        when(koodiService.getKoodiVersio(KOODI_URI, versio)).thenReturn(getMatchingCodeElementVersion(versio, versios));
        if (compareToLatestAccepted) {
            returnGivenKoodiVersiosWithKoodiFromMockedKoodiService(versios);
        } else {
            returnLatestKoodiVersioFromMockedKoodiService(versios);
        }
        koodistoServiceReturnsKoodistoWithAllVersions(versios);
        return service.getChangesDto(KOODI_URI, versio, compareToLatestAccepted);
    }

    private KoodiChangesDto givenResultWithMultipleKoodiVersiosForDateQuery(Date date, boolean compareToLatestAccepted, KoodiVersio ... versios) {
        if (!compareToLatestAccepted) {
            returnLatestKoodiVersioFromMockedKoodiService(versios);
        }
        returnGivenKoodiVersiosWithKoodiFromMockedKoodiService(versios);
        koodistoServiceReturnsKoodistoWithAllVersions(versios);
        return service.getChangesDto(KOODI_URI, new DateTime(date), compareToLatestAccepted);
    }
    
    private void koodistoServiceReturnsKoodistoWithAllVersions(KoodiVersio ... versios) {
        Set<KoodistoVersio> koodistoVersios = new HashSet<>();
        for (KoodiVersio kv : versios) {
            koodistoVersios.add(kv.getKoodistoVersios().iterator().next().getKoodistoVersio());
        }        
        Koodisto koodisto = Mockito.mock(Koodisto.class);
        when(koodisto.getKoodistoVersios()).thenReturn(koodistoVersios);
        when(koodistoService.getKoodistoByKoodistoUri(any())).thenReturn(koodisto);
    }
    
    private KoodiVersio getMatchingCodeElementVersion(Integer versio, KoodiVersio[] versios) {
        for (KoodiVersio koodiVersio : versios) {
            if(versio.equals(koodiVersio.getVersio()))
                return koodiVersio;
        }
        return versios[0];
    }

    void returnGivenKoodiVersiosWithKoodiFromMockedKoodiService(KoodiVersio... versios) {
        Koodi koodi = Mockito.mock(Koodi.class);
        when(koodi.getKoodiVersios()).thenReturn(new HashSet<KoodiVersio>(Arrays.asList(versios)));
        when(koodiService.getKoodi(KOODI_URI)).thenReturn(koodi);
    }
    
    private void returnLatestKoodiVersioFromMockedKoodiService(KoodiVersio... versios) {
        KoodiVersio latest = null;
        for(KoodiVersio kv : versios) {
            latest = latest == null || kv.getVersio() > latest.getVersio() ? kv : latest;
        }
        when(koodiService.getLatestKoodiVersio(KOODI_URI)).thenReturn(latest);
    }
    
    private KoodiVersio givenKoodiVersio(Integer versio) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio).build();        
    }
    
    private KoodiVersio givenKoodiVersio(Integer versio, String koodiUri) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(koodiUri, versio).build();        
    }
    
    private KoodiVersio givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(Integer versio, String name, String shortName, String description, Kieli language) {
        return givenKoodiVersioWithMetadata(versio, givenKoodiMetadata(name, shortName, description, language));
    }
    
    private KoodiVersio givenKoodiVersioWithMetadata(Integer versio, KoodiMetadata ... datas) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio).addMetadata(datas).build();
    }
    
    private KoodiVersio givenKoodiVersioWithTilaAndMetadata(Integer versio, Tila tila, KoodiMetadata ... datas) {
        KoodiVersio kv = givenKoodiVersioWithMetadata(versio, datas);
        kv.setTila(tila);
        kv.getKoodistoVersios().iterator().next().getKoodistoVersio().setTila(tila);
        return kv;
    }
    
    private KoodiMetadata givenKoodiMetadata(String name, String shortName, String description, Kieli language) {
        return DtoFactory.createKoodiMetadata(name, shortName, description, language);
    }
    
    private KoodiVersio givenKoodiVersioWithCustomDatesItIsInEffect(int versio, Date startDate, Date endDate) {
        return DtoFactory.createKoodiVersioWithoutMetadatasWithStartAndEndDates(KOODI_URI, versio, startDate, endDate).build();
    }
    
    private KoodiVersio givenKoodiVersioWithMetaDataAndCustomDateItWasCreated(int versio, Date created, KoodiMetadata ... datas) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio).addMetadata(datas).setCreated(created).build();
    }
    
    private KoodiVersio givenKoodiVersioWithRelations(int versio, KoodinSuhde ... relations) {
        KoodiVersioBuilder builder = DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio);
        for(KoodinSuhde ks : relations){
            if(ks.getAlakoodiVersio() == null) {
                builder.addParentRelation(ks);
            } else {
                builder.addChildRelation(ks);
            }
        }
        return builder.build();
    }
    
    private KoodinSuhde givenKoodinSuhde(SuhteenTyyppi tyyppi, KoodiVersio parent, KoodiVersio child) {
        return givenPassiveKoodinSuhde(tyyppi, parent, child, false, false);
    }
    
    private KoodinSuhde givenPassiveKoodinSuhde(SuhteenTyyppi tyyppi, KoodiVersio parent, KoodiVersio child, boolean parentPassive, boolean childPassive) {
        return DtoFactory.createKoodinSuhde(tyyppi, child, parent, parentPassive, childPassive);
    }
}
