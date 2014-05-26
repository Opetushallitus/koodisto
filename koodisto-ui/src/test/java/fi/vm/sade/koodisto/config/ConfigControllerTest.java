package fi.vm.sade.koodisto.config;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:spring/application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigControllerTest {

	@Autowired
	private ConfigController controller;

	@Test
	public void controllerShouldSetupJSVariables() throws Exception {
		String result = controller.index();
		assertTrue(result.contains("SESSION_KEEPALIVE_INTERVAL_IN_SECODS = \"60\";"));
		assertTrue(result.contains("ORGANIZATION_SERVICE_URL_BASE = \"https://itest-virkailija.oph.ware.fi/organisaatio-service/\";"));
		assertTrue(result.contains("CAS_URL = \"/cas/myroles\";"));
		assertTrue(result.contains("SESSION_KEEPALIVE_INTERVAL_IN_SECODS = \"60\";"));
		assertTrue(result.contains("AUTH_MODE = \"dev\";"));
	}


}
