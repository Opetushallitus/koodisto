package fi.vm.sade.koodisto.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoMetadataRepositoryIT {

    @Autowired
    KoodistoMetadataRepository koodistoMetadataRepository;

    @Test
    public void testFindByKoodistoUri() {

        List<KoodistoMetadata> koodistoMetadataList = koodistoMetadataRepository
                .findByKoodistoUri("http://www.avi.fi/aluevirasto");
        assertNotNull(koodistoMetadataList);
    }

    @Test
    public void testExistsByKoodistoUriOtherThanAndNimi() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        final String nimi = "paljon versioita koodistossa";

        assertFalse(koodistoMetadataRepository.existsByKoodistoUriOtherThanAndNimi(koodistoUri, nimi));

        final String anotherKoodistoUri = "http://testikoodisto.fi";
        assertTrue(koodistoMetadataRepository.existsByKoodistoUriOtherThanAndNimi(anotherKoodistoUri, nimi));
    }

}
