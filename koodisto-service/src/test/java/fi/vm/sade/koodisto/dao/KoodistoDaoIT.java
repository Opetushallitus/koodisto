package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.Koodisto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoDaoIT {

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
