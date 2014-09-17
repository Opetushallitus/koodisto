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

import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:spring/simple-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoChangesServiceImplTest {
    
    private final static String KOODISTO_URI = "lehtipuut";
    private final static String NAME = "Lehtipuut", DESCRIPTION = "pudottavat lehtensä syksyisin";
    private final static String NAME_EN = "Leaftrees", DESCRIPTION_EN = "leaves are dropped during autumn";
    private final static Date CURRENT_DATE = new Date();
    
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
        String newName = "koivu";
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
    
    private KoodistoChangesDto givenResult(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        Integer versio = koodistoVersio.getVersio();
        when(koodistoService.getKoodistoVersio(KOODISTO_URI, versio)).thenReturn(koodistoVersio);
        when(koodistoService.getLatestKoodistoVersio(KOODISTO_URI)).thenReturn(latest);
        return service.getChangesDto(KOODISTO_URI, versio, false);
    }
    
    private KoodistoVersio givenKoodistoVersio(Integer versio) {
        return DtoFactory.createKoodistoVersio(new Koodisto(), versio).build();
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
}
