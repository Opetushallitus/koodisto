package fi.vm.sade.koodisto.dao;


import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("classpath:test-data.xml")
public class KoodiMetadataDaoTest extends DaoTest {

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
