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
        int versio = 1;
        assertResultIsNoChanges(givenResult(givenKoodistoVersio(versio), givenKoodistoVersio(versio)), versio);
    }

    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        int versio = 1;
        assertResultIsNoChanges(givenResult(givenKoodistoVersio(versio), givenKoodistoVersio(versio + 1)), versio + 1);
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
    
}
