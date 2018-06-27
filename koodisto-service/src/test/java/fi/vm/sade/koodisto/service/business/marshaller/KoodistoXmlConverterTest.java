package fi.vm.sade.koodisto.service.business.marshaller;

import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoodistoXmlConverterTest extends AbstractKoodistoConverterTest {

    @Autowired
    private KoodistoXmlConverter koodistoXmlConverter;

    @Override
    protected KoodistoConverter getConverter() {
        return koodistoXmlConverter;
    }

    @Override
    protected String getTestFile() {
        return "jhs_xml_example.xml";
    }
}
