package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.MuutosTila;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:spring/simple-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodiChangesDtoBusinessServiceImplTest {
    
    private static final Date CURRENT_DATE = new Date();

    private static final String KOODI_URI = "uri";
    
    private static final String NAME = "elefantti", SHORT_NAME = "fantti", DESCRIPTION = "kärsäeläin";
    private static final String NAME_EN = "African elephant", SHORT_NAME_EN = "elephant", DESCRIPTION_EN = "trunkard";
    private static final String NAME_SV = "elefant", SHORT_NAME_SV = "kort", DESCRIPTION_SV = "stora flockdjur";

    @ReplaceWithMock
    @Autowired
    private KoodiBusinessService koodiService;
    
    @Autowired
    private KoodiChangesDtoBusinessService service;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
        
    @Test
    public void returnsNoChangesIfNothingHasChanged() {
        int versio = 1;
        assertResultIsNoChanges(givenResult(givenKoodiVersio(versio), givenKoodiVersio(versio)), versio);
    }


    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        int versio = 1;
        assertResultIsNoChanges(givenResult(givenKoodiVersio(versio), givenKoodiVersio(versio + 1)), versio + 1);
    }
    
    @Test
    public void returnsNoChangesIfVersionHasNotChangedButThereAreOtherChanges() {
        int versio = 5;
        KoodiVersio withChanges = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio, "hippopotamus", "hippo", "large water-dwelling mammal", Kieli.EN);
        assertResultIsNoChanges(givenResult(withChanges, givenKoodiVersio(versio)), versio);
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
        int versio = 1;
        KoodiVersio latest = givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(versio + 1, NAME, SHORT_NAME, DESCRIPTION, Kieli.FI);
        KoodiVersio original = givenKoodiVersio(versio);
        assertResultHasMetadataChanges(givenResult(original, latest), versio + 1, new SimpleKoodiMetadataDto(NAME, Kieli.FI, DESCRIPTION, SHORT_NAME));
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
        KoodiVersio latest = givenKoodiVersio(versio);
        KoodiVersio original = givenKoodiVersioWithCustomDatesItIsInEffect(versio + 1, CURRENT_DATE, CURRENT_DATE);
        KoodiChangesDto result = givenResult(original, latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertNull(result.voimassaLoppuPvm);
        assertTrue(result.poistettuVoimassaLoppuPvm);
    }
    
    @Test
    public void returnsHasChangedIfTilaHasChanged() {
        int versio = 1;
        KoodiVersio latest = givenKoodiVersio(versio + 1);
        latest.setTila(Tila.PASSIIVINEN);
        KoodiChangesDto result = givenResult(givenKoodiVersio(versio), latest);
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(Tila.PASSIIVINEN, result.tila);
    }
        
    private void assertResultIsNoChanges(KoodiChangesDto result, int versio) {
        assertEquals(KoodiChangesDto.MuutosTila.EI_MUUTOKSIA, result.muutosTila);
        assertEquals(versio, result.viimeisinVersio.intValue());
        assertNull(result.lisatytKoodinSuhteet);
        assertNull(result.poistetutKoodinSuhteet);
        assertTrue(result.muuttuneetTiedot.isEmpty());
        assertNotNull(result.viimeksiPaivitetty);
        assertNull(result.voimassaAlkuPvm);
        assertNull(result.voimassaLoppuPvm);
        assertNull(result.tila);
    }
    
    private void assertResultHasMetadataChanges(KoodiChangesDto result, int versio, SimpleKoodiMetadataDto ... expecteds) {
        assertEquals(KoodiChangesDto.MuutosTila.MUUTOKSIA, result.muutosTila);
        assertTrue(result.muuttuneetTiedot.containsAll(Arrays.asList(expecteds)));
        assertEquals(versio, result.viimeisinVersio.intValue());
    }
    
    private KoodiChangesDto givenResult(KoodiVersio koodiVersio, KoodiVersio latest) {
        Integer versio = koodiVersio.getVersio();
        when(koodiService.getKoodiVersio(KOODI_URI, versio)).thenReturn(koodiVersio);
        when(koodiService.getLatestKoodiVersio(KOODI_URI)).thenReturn(latest);
        return service.getChangesDto(KOODI_URI, versio);
    }
    
    private KoodiVersio givenKoodiVersio(Integer versio) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio).build();        
    }
    
    private KoodiVersio givenKoodiVersioWithCustomNameShortNameAndDescriptionForLanguage(Integer versio, String name, String shortName, String description, Kieli language) {
        return givenKoodiVersioWithMetadata(versio, givenKoodiMetadata(name, shortName, description, language));
    }
    
    private KoodiVersio givenKoodiVersioWithMetadata(Integer versio, KoodiMetadata ... datas) {
        return DtoFactory.createKoodiVersioWithUriAndVersioWithoutMetadatas(KOODI_URI, versio).addMetadata(datas).build();
    }
    
    private KoodiMetadata givenKoodiMetadata(String name, String shortName, String description, Kieli language) {
        return DtoFactory.createKoodiMetadata(name, shortName, description, language);
    }
    
    private KoodiVersio givenKoodiVersioWithCustomDatesItIsInEffect(int versio, Date startDate, Date endDate) {
        return DtoFactory.createKoodiVersioWithoutMetadatasWithStartAndEndDates(KOODI_URI, versio, startDate, endDate).build();
    }
}
