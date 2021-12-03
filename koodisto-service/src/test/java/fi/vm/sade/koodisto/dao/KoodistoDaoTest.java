package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.Koodisto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@DatabaseSetup("classpath:test-data.xml")
public class KoodistoDaoTest extends DaoTest {

    @Autowired
    private KoodistoDAO koodistoDAO;

    @Test
    public void testReadByUri() {
        Koodisto k = koodistoDAO.readByUri("http://www.avi.fi/aluevirasto");
        assertNotNull(k);
    }

    @Test
    public void testKoodistoUriExists() {
        final String koodistoUri = "http://ekaversioluonnostilassa";
        assertTrue(koodistoDAO.koodistoUriExists(koodistoUri));
        assertFalse(koodistoDAO.koodistoUriExists("not exists"));
    }
}
