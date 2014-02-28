package fi.vm.sade.koodisto.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
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

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
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
