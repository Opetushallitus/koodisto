package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KoodiBusinessServiceImplTest {


    @Test
    void testRelationEvaluators() {
        KoodiVersio compareKoodiVersio = mock(KoodiVersio.class);
        Koodi compareKoodi = mock(Koodi.class);
        when(compareKoodiVersio.getKoodi()).thenReturn(compareKoodi);
        KoodinSuhde koodinSuhde = createSuhde(SuhteenTyyppi.SISALTYY, 1L, 2L);

        when(compareKoodi.getId()).thenReturn(1L);
        assertTrue(KoodiBusinessServiceImpl.sisaltaa(compareKoodiVersio, false, koodinSuhde));
        assertFalse(KoodiBusinessServiceImpl.sisaltaa(compareKoodiVersio, true, koodinSuhde));
        when(compareKoodi.getId()).thenReturn(2L);
        assertTrue(KoodiBusinessServiceImpl.sisaltyy(compareKoodiVersio, true, koodinSuhde));
        assertFalse(KoodiBusinessServiceImpl.sisaltyy(compareKoodiVersio, false, koodinSuhde));
        when(compareKoodi.getId()).thenReturn(3L);
        assertFalse(KoodiBusinessServiceImpl.sisaltyy(compareKoodiVersio, true, koodinSuhde));
        assertFalse(KoodiBusinessServiceImpl.sisaltyy(compareKoodiVersio, false, koodinSuhde));
    }

    @Test
    void testGetOldRelationsByRelationType() {
        long thisId = 0L;
        KoodiVersio koodiVersio = createKoodiVersio(thisId);
        koodiVersio.setAlakoodis(Stream.of(
                Stream.of(1L, 2L).map(a ->
                        createSuhde(SuhteenTyyppi.SISALTYY, thisId, a)).collect(Collectors.toSet()),
                Stream.of(5L).map(a ->
                        createSuhde(SuhteenTyyppi.RINNASTEINEN, thisId, a)).collect(Collectors.toSet())
        ).flatMap(Collection::stream).collect(Collectors.toSet()));

        koodiVersio.setYlakoodis(Stream.of(
                Stream.of(9L, 10L, 11L).map(a ->
                        createSuhde(SuhteenTyyppi.SISALTYY, a, thisId)).collect(Collectors.toSet()),
                Stream.of(13L, 14L, 15L).map(a ->
                        createSuhde(SuhteenTyyppi.RINNASTEINEN, a, thisId)).collect(Collectors.toSet())
        ).flatMap(Collection::stream).collect(Collectors.toSet()));

        assertEquals(2, KoodiBusinessServiceImpl.getOldRelationsByRelationType(SuhteenTyyppi.SISALTYY, false, koodiVersio).size());
        assertEquals(3, KoodiBusinessServiceImpl.getOldRelationsByRelationType(SuhteenTyyppi.SISALTYY, true, koodiVersio).size());
        assertEquals(4, KoodiBusinessServiceImpl.getOldRelationsByRelationType(SuhteenTyyppi.RINNASTEINEN, true, koodiVersio).size());
    }

    private KoodinSuhde createSuhde(SuhteenTyyppi suhteenTyyppi, long ylaKoodiId, long alaKoodiId) {
        KoodinSuhde suhde = new KoodinSuhde();
        suhde.setSuhteenTyyppi(suhteenTyyppi);
        suhde.setYlakoodiVersio(createKoodiVersio(ylaKoodiId));
        suhde.setAlakoodiVersio(createKoodiVersio(alaKoodiId));
        return suhde;
    }

    private KoodiVersio createKoodiVersio(long id) {
        Koodi koodi = new Koodi();
        koodi.setId(id);
        KoodiVersio versio = new KoodiVersio();
        versio.setKoodi(koodi);
        return versio;
    }
}