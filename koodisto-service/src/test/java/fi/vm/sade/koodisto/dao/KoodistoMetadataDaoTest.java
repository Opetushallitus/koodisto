package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@DatabaseSetup("classpath:test-data.xml")
public class KoodistoMetadataDaoTest extends DaoTest {

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
