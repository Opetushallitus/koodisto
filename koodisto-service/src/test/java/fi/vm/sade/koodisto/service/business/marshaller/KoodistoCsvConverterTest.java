package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 13.19
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoCsvConverterTest extends AbstractKoodistoConverterTest {
    @Autowired
    private KoodistoCsvConverter koodistoCsvConverter;

    @Override
    protected KoodistoConverter getConverter() {
        return koodistoCsvConverter;
    }

    @Override
    protected String getTestFile() {
        return "csv_example.csv";
    }
}
