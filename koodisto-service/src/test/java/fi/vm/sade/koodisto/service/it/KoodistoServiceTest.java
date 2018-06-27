package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class KoodistoServiceTest {

    @Autowired
    KoodistoService koodistoService;

    @Test
    public void searchLatestAccepted() {
        final String koodistoUri = "http://testikoodisto.fi";
        final TilaType hyvaksyttyTila = TilaType.HYVAKSYTTY;

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder
                .latestAcceptedKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(1, koodistos.get(0).getVersio());
        assertEquals(hyvaksyttyTila, koodistos.get(0).getTila());

        final TilaType passiivinenTila = TilaType.PASSIIVINEN;
        criteriaType.getKoodistoTilas().clear();
        criteriaType.getKoodistoTilas().add(passiivinenTila);

        koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(2, koodistos.get(0).getVersio());
        assertEquals(passiivinenTila, koodistos.get(0).getTila());
    }

    @Test
    public void searchLatestValidAccepted() {
        final String koodistoUri = "http://montapassiivistauuttaversiota";
        final int koodistoVersio = 1;
        final TilaType hyvaksyttyTila = TilaType.HYVAKSYTTY;

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder
                .latestValidAcceptedKoodistoByUri(koodistoUri);
        List<KoodistoType> koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(koodistoVersio, koodistos.get(0).getVersio());
        assertEquals(hyvaksyttyTila, koodistos.get(0).getTila());
    }

    @Test
    public void searchAllKoodistoVersions() {
        final String koodistoUri = "http://testikoodisto.fi";

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder
                .koodistoVersiosByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(2, koodistos.size());

        Collections.sort(koodistos, new Comparator<KoodistoType>() {

            @Override
            public int compare(KoodistoType o1, KoodistoType o2) {
                return Integer.valueOf(o1.getVersio()).compareTo(o2.getVersio());
            }
        });

        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(koodistoUri, koodistos.get(1).getKoodistoUri());
        assertEquals(1, koodistos.get(0).getVersio());
        assertEquals(2, koodistos.get(1).getVersio());
    }

    @Test
    public void testSearchMultipleKoodistos() {
        final String koodistoUri1 = "http://testikoodisto.fi";
        final String koodistoUri2 = "http://montapassiivistauuttaversiota";

        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistosByUri(
                koodistoUri1, koodistoUri2);
        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        assertEquals(2, koodistos.size());
    }

    @Test
    public void searchKoodistoByUri() {
        final String koodistoUri = "http://testikoodisto.fi";
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder
                .latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(2, koodistos.get(0).getVersio());
    }

    @Test
    public void searchKoodistoByUriAndVersio() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Integer koodistoVersio = 1;
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(
                koodistoUri, koodistoVersio);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodistoUri());
        assertEquals(koodistoVersio, Integer.valueOf(koodistos.get(0).getVersio()));
    }
}
