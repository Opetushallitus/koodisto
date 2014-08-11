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

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.when;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:spring/simple-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodiChangesDtoBusinessServiceImplTest {
    
    private static final String KOODI_URI = "uri";

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
        assertResultIsNoChanges(givenNoChangesResult(givenKoodiVersio(versio), givenKoodiVersio(versio)), versio);
    }


    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        int versio = 1;
        assertResultIsNoChanges(givenNoChangesResult(givenKoodiVersio(versio), givenKoodiVersio(versio + 1)), versio + 1);
    }
    
    @Test
    public void returnsNoChangesIfVersionHasNotChangedButThereAreOtherChanges() {
        
    }
    
    @Test
    public void returnsHasChangedIfNameHasChanged() {
        
    }
    
    @Test
    public void returnsHasChangedIfShortNameHasChanged() {
        
    }
    
    @Test
    public void returnsHasChangedIfDescriptionHasChanged() {
        
    }
    
    @Test
    public void returnsHasChangedIfMultipleMetadataHasChanged() {
        
    }
    
    private void assertResultIsNoChanges(KoodiChangesDto result, int versio) {
        assertEquals(KoodiChangesDto.MuutosTila.EI_MUUTOKSIA, result.muutosTila);
        assertEquals(versio, result.viimeisinVersio.intValue());
        assertNull(result.lisatytKoodinSuhteet);
        assertNull(result.poistetutKoodinSuhteet);
        assertNull(result.muuttuneetTiedot);
        assertNull(result.viimeksiPaivitetty);
        assertNull(result.voimassaAlkuPvm);
        assertNull(result.voimassaLoppuPvm);
        assertNull(result.tila);
    }
    
    private KoodiChangesDto givenNoChangesResult(KoodiVersio koodiVersio, KoodiVersio latest) {
        Integer versio = koodiVersio.getVersio();
        when(koodiService.getKoodiVersio(KOODI_URI, versio)).thenReturn(koodiVersio);
        when(koodiService.getLatestKoodiVersio(KOODI_URI)).thenReturn(latest);
        return service.getChangesDto(KOODI_URI, versio);
    }
    
    private KoodiVersio givenKoodiVersio(Integer versio) {
        return DtoFactory.createKoodiVersioWithUriAndVersio(KOODI_URI, versio).build();        
    }
    
}
