package fi.vm.sade.koodisto.repository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

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
public class KoodistoRepositoryIT {

    @Autowired
    private KoodistoRepository koodistoRepository;

    @Test
    public void testReadByUri() {
        assertTrue(koodistoRepository.findByKoodistoUri("http://www.avi.fi/aluevirasto").isPresent());
    }

    @Test
    public void testKoodistoUriExists() {
        final String koodistoUri = "http://ekaversioluonnostilassa";
        assertTrue(koodistoRepository.existsByKoodistoUri(koodistoUri));
        assertFalse(koodistoRepository.existsByKoodistoUri("not exists"));
    }
}
