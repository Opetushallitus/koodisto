package fi.vm.sade.koodisto.service.business.marshaller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestPropertySource(locations = "classpath:application.properties")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoodistoXmlConverterIT extends AbstractKoodistoConverterTest {

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
