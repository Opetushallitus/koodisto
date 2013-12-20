package fi.vm.sade.koodisto.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class KoodistoDaoTest {

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
