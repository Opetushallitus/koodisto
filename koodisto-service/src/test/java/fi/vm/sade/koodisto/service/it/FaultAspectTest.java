package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosVersioSelectionType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class FaultAspectTest {

    @Autowired
    private KoodistoService koodistoService;

    @Test(expected = GenericFault.class)
    public void test() {
        SearchKoodistosCriteriaType criteria = new SearchKoodistosCriteriaType();
        criteria.getKoodistoUris().add("puuppa");
        criteria.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.SPECIFIC);
        koodistoService.searchKoodistos(criteria);
    }
}
