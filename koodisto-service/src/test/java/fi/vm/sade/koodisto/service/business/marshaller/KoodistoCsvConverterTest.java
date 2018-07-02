package fi.vm.sade.koodisto.service.business.marshaller;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
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
