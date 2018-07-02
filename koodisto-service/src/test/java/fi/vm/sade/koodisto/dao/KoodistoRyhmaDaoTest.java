package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoRyhmaDaoTest {

    @Autowired
    KoodistoRyhmaDAO koodistoRyhmaDAO;

    @Test
    public void testListAllKoodistoRyhmas() {

        List<KoodistoRyhma> koodistoRyhmas = koodistoRyhmaDAO.listAllKoodistoRyhmas();
        assertNotNull(koodistoRyhmas);
        assertEquals(4, koodistoRyhmas.size());
    }

}
