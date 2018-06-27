package fi.vm.sade.koodisto.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class KoodiMetadataDaoTest {

    @Autowired
    private KoodiMetadataDAO koodiMetadataDAO;

    @Test
    public void testNimiExistsForSomeOtherKoodi() {
        final String koodiUri = "435";
        final String nimi = "koodi 1 versio 1";

        assertFalse(koodiMetadataDAO.nimiExistsForSomeOtherKoodi(koodiUri, nimi));

        final String anotherKoodiUri = "436";
        assertTrue(koodiMetadataDAO.nimiExistsForSomeOtherKoodi(anotherKoodiUri, nimi));
    }

    @Test
    public void testNimiExistsInKoodisto() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String nimi = "haapavesi";
        assertTrue(koodiMetadataDAO.nimiExistsInKoodisto(koodistoUri, nimi));

        final String anotherKoodistoUri = "http://www.kunnat.fi/maakunta";
        assertFalse(koodiMetadataDAO.nimiExistsInKoodisto(anotherKoodistoUri, nimi));
    }

    @Test
    public void testNimiExistsInKoodistoForSomeOtherKoodi() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String koodiUri = "43";
        final String nimi = "haapavesi";
        assertFalse(koodiMetadataDAO.nimiExistsInKoodistoForSomeOtherKoodi(koodistoUri, koodiUri, nimi));

        final String anotherKoodiUri = "44";
        assertTrue(koodiMetadataDAO.nimiExistsInKoodistoForSomeOtherKoodi(koodistoUri, anotherKoodiUri, nimi));

        final String anotherKoodistoUri = "http://www.kunnat.fi/maakunta";
        assertFalse(koodiMetadataDAO.nimiExistsInKoodistoForSomeOtherKoodi(anotherKoodistoUri, anotherKoodiUri, nimi));
    }
}
