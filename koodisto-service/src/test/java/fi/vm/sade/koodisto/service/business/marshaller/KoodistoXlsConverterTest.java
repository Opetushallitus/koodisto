package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoXlsConverterTest extends AbstractKoodistoConverterTest {
    @Autowired
    private KoodistoXlsConverter koodistoXlsConverter;

    @Override
    protected KoodistoConverter getConverter() {
        return new DummyXlsConverter();
    }

    @Override
    protected String getTestFile() {
        return "excel_example.xls";
    }

    private static class DummyXlsConverter extends KoodistoXlsConverter {

        @Override
        protected void postprocess(HSSFSheet sheet) {

        }

    }
}
