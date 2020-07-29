package fi.vm.sade.koodisto.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.repository.KoodistoVersioRepository;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoVersioRepositoryIT {

    @Autowired
    private KoodistoVersioRepository koodistoVersioRepository;

    @Test
    public void searchLatestAccepted() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Tila hyvaksyttyTila = Tila.HYVAKSYTTY;

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.latestAcceptedKoodistoByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioRepository.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(1), koodistos.get(0).getVersio());
        assertEquals(hyvaksyttyTila, koodistos.get(0).getTila());

        final Tila passiivinenTila = Tila.PASSIIVINEN;
        criteriaType.getKoodistoTilas().clear();
        criteriaType.getKoodistoTilas().add(TilaType.valueOf(passiivinenTila.name()));

        koodistos = koodistoVersioRepository.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(2), koodistos.get(0).getVersio());
        assertEquals(passiivinenTila, koodistos.get(0).getTila());
    }

    @Test
    public void searchAllKoodistoVersions() {
        final String koodistoUri = "http://testikoodisto.fi";

        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.koodistoVersiosByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioRepository.searchKoodistos(criteriaType);
        assertEquals(2, koodistos.size());

        koodistos.sort(Comparator.comparing(KoodistoVersio::getVersio));

        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(koodistoUri, koodistos.get(1).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(1), koodistos.get(0).getVersio());
        assertEquals(Integer.valueOf(2), koodistos.get(1).getVersio());
    }

    @Test
    public void searchKoodistoByUri() {
        final String koodistoUri = "http://testikoodisto.fi";
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoVersio> koodistos = koodistoVersioRepository.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(Integer.valueOf(2), koodistos.get(0).getVersio());
    }

    @Test
    public void searchKoodistoByUriAndVersio() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Integer koodistoVersio = 1;
        SearchKoodistosCriteriaType criteriaType = KoodistoServiceSearchCriteriaBuilder.koodistoByUriAndVersio(koodistoUri, koodistoVersio);

        List<KoodistoVersio> koodistos = koodistoVersioRepository.searchKoodistos(criteriaType);
        assertEquals(1, koodistos.size());
        assertEquals(koodistoUri, koodistos.get(0).getKoodisto().getKoodistoUri());
        assertEquals(koodistoVersio, koodistos.get(0).getVersio());
    }

    @Test
    public void testGetPreviousKoodistoVersio() {
        final String koodistoUri ="http://koodisto8";
        final Integer koodistoVersio = 4;

        final Integer previousVersio = 2;
        KoodistoVersio previous = koodistoVersioRepository.getPreviousKoodistoVersio(koodistoUri, koodistoVersio).orElseThrow();
        assertEquals(previousVersio, previous.getVersio());
        assertEquals(koodistoUri, previous.getKoodisto().getKoodistoUri());
    }
}
