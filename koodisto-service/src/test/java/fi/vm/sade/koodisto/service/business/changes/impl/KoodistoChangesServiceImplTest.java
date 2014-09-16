package fi.vm.sade.koodisto.service.business.changes.impl;

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
import fi.vm.sade.koodisto.model.KoodistoVersio;
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
        KoodistoChangesDto result = givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersioWithMetadata(VERSIO+1, newName, DtoFactory.KOODISTO_DESCRIPTION));
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(1, result.muuttuneetTiedot.size());
        assertEquals(new SimpleMetadataDto(newName, Kieli.FI, null), result.muuttuneetTiedot.get(0));
    }
    
    @Test
    public void returnsHasChangedIfDescriptionHasChanged() {
        String newDescription = "parempi kuvaus";
        KoodistoChangesDto result = givenResult(givenKoodistoVersio(VERSIO), givenKoodistoVersioWithMetadata(VERSIO+1, DtoFactory.KOODISTO_NAME, newDescription));
        assertEquals(MuutosTila.MUUTOKSIA, result.muutosTila);
        assertEquals(1, result.muuttuneetTiedot.size());
        assertEquals(new SimpleMetadataDto(null, Kieli.FI, newDescription), result.muuttuneetTiedot.get(0));
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
    
    private KoodistoChangesDto givenResult(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        Integer versio = koodistoVersio.getVersio();
        when(koodistoService.getKoodistoVersio(KOODISTO_URI, versio)).thenReturn(koodistoVersio);
        when(koodistoService.getLatestKoodistoVersio(KOODISTO_URI)).thenReturn(latest);
        return service.getChangesDto(KOODISTO_URI, versio, false);
    }
    
    private KoodistoVersio givenKoodistoVersio(Integer versio) {
        return DtoFactory.createKoodistoVersio(new Koodisto(), versio);
    }
    
    private KoodistoVersio givenKoodistoVersioWithMetadata(Integer versio, String name, String description) {
        return DtoFactory.createKoodistoVersioWithMetadata(new Koodisto(), versio, name, description, Kieli.FI);
    }
    
}
