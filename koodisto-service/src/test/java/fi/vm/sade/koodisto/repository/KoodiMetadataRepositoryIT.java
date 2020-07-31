package fi.vm.sade.koodisto.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodiMetadataRepositoryIT {

    @Autowired
    private KoodiMetadataRepository koodiMetadataRepository;

    @Test
    public void testExistsByNimiAndKoodiUriOtherThan() {
        final String koodiUri = "435";
        final String nimi = "koodi 1 versio 1";

        assertFalse(koodiMetadataRepository.existsByNimiAndKoodiUriOtherThan(nimi, koodiUri));

        final String anotherKoodiUri = "436";
        assertTrue(koodiMetadataRepository.existsByNimiAndKoodiUriOtherThan(nimi, anotherKoodiUri));
    }

    @Test
    public void testExistsByNimiAndKoodistoUri() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String nimi = "haapavesi";
        assertTrue(koodiMetadataRepository.existsByNimiAndKoodistoUri(nimi, koodistoUri));

        final String anotherKoodistoUri = "http://www.kunnat.fi/maakunta";
        assertFalse(koodiMetadataRepository.existsByNimiAndKoodistoUri(nimi, anotherKoodistoUri));
    }

    @Test
    public void testExistsByNimiAndKoodistoUriAndKoodiUriOtherThan() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String koodiUri = "43";
        final String nimi = "haapavesi";
        assertFalse(koodiMetadataRepository.existsByNimiAndKoodistoUriAndKoodiUriOtherThan(
                nimi, koodistoUri, koodiUri));

        final String anotherKoodiUri = "44";
        assertTrue(koodiMetadataRepository.existsByNimiAndKoodistoUriAndKoodiUriOtherThan(
                nimi, koodistoUri, anotherKoodiUri));

        final String anotherKoodistoUri = "http://www.kunnat.fi/maakunta";
        assertFalse(koodiMetadataRepository.existsByNimiAndKoodistoUriAndKoodiUriOtherThan(
                nimi, anotherKoodistoUri, anotherKoodiUri));
    }
}
