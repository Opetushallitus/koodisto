package fi.vm.sade.koodisto.config;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigControllerTest {

    @Autowired
    private ConfigController controller;

    @Test
    public void controllerShouldSetupJSVariables() {
        String result = controller.frontProperties();
        assertTrue(result.contains("window.urlProperties="));
    }
}
