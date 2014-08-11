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
        String koodiUri = "uri";
        when(koodiService.getKoodiVersio(koodiUri, versio)).thenReturn(givenKoodiVersio(koodiUri, versio));
        when(koodiService.getLatestKoodiVersio(koodiUri)).thenReturn(givenKoodiVersio(koodiUri, versio));
        KoodiChangesDto result = service.getChangesDto(koodiUri, versio);
        assertResultIsNoChanges(result, versio);
    }

    @Test
    public void returnsNoChangesIfOnlyVersionHasChanged() {
        int versio = 1;
        String koodiUri = "uri";
        when(koodiService.getKoodiVersio(koodiUri, versio)).thenReturn(givenKoodiVersio(koodiUri, versio));
        when(koodiService.getLatestKoodiVersio(koodiUri)).thenReturn(givenKoodiVersio(koodiUri, versio + 1));
        KoodiChangesDto result = service.getChangesDto(koodiUri, versio);
        assertResultIsNoChanges(result, versio + 1);
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
    
    private KoodiVersio givenKoodiVersio(String uri, Integer versio) {
        return DtoFactory.createKoodiVersioWithUriAndVersio(uri, versio).build();        
    }
    
}
