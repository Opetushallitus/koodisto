package fi.vm.sade.koodisto.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
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
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class KoodistoVersioDaoTest {

    @Autowired
    KoodistoVersioDAO koodistoVersioDAO;

    @Test
    public void searchLatestAccepted() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Tila hyvaksyttyTila = Tila.HYVAKSYTTY;

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.latestAcceptedKoodistoByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioDAO.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(1), koodistos.get(0).getVersio());
        assertEquals(hyvaksyttyTila, koodistos.get(0).getTila());

        final Tila passiivinenTila = Tila.PASSIIVINEN;
        criteriaType.getKoodistoTilas().clear();
        criteriaType.getKoodistoTilas().add(TilaType.valueOf(passiivinenTila.name()));

        koodistos = koodistoVersioDAO.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(2), koodistos.get(0).getVersio());
        assertEquals(passiivinenTila, koodistos.get(0).getTila());
    }

    @Test
    public void searchAllKoodistoVersions() {
        final String koodistoUri = "http://testikoodisto.fi";

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.koodistoVersiosByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioDAO.searchKoodistos(criteriaType);
        assertEquals(2, koodistos.size());

        Collections.sort(koodistos, new Comparator<KoodistoVersio>() {

            @Override
            public int compare(KoodistoVersio o1, KoodistoVersio o2) {
                return o1.getVersio().compareTo(o2.getVersio());
            }
        });

        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(koodistoUri, koodistos.get(1).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(1), koodistos.get(0).getVersio());
        assertEquals(Integer.valueOf(2), koodistos.get(1).getVersio());
    }

    @Test
    public void searchKoodistoByUri() {
        final String koodistoUri = "http://testikoodisto.fi";
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioDAO.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(2), koodistos.get(0).getVersio());
    }

    @Test
    public void searchKoodistoByUriAndVersio() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Integer koodistoVersio = 1;
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(koodistoUri, koodistoVersio);

        List<KoodistoVersio> koodistos = koodistoVersioDAO.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(koodistoVersio, koodistos.get(0).getVersio());
    }

    @Test
    public void testGetPreviousKoodistoVersio() {
        final String koodistoUri ="http://koodisto8";
        final Integer koodistoVersio = 4;

        final Integer previousVersio = 2;
        KoodistoVersio previous = koodistoVersioDAO.getPreviousKoodistoVersio(koodistoUri, koodistoVersio);
        assertEquals(previousVersio, previous.getVersio());
        assertEquals(koodistoUri, previous.getKoodisto().getKoodistoUri());
    }
}
