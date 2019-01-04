package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoMetadataDaoTest {

    @Autowired
    KoodistoMetadataDAO koodistoMetadataDAO;

    @Test
    public void testListAllByKoodisto() {

        List<KoodistoMetadata> koodistoMetadataList = koodistoMetadataDAO
                .listAllByKoodisto("http://www.avi.fi/aluevirasto");
        assertNotNull(koodistoMetadataList);
    }

    @Test
    public void testNimiExistsForSomeOtherKoodisto() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        final String nimi = "paljon versioita koodistossa";

        assertFalse(koodistoMetadataDAO.nimiExistsForSomeOtherKoodisto(koodistoUri, nimi));

        final String anotherKoodistoUri = "http://testikoodisto.fi";
        assertTrue(koodistoMetadataDAO.nimiExistsForSomeOtherKoodisto(anotherKoodistoUri, nimi));
    }

}
