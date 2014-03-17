package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class KoodistoConfigurationTest {

    @Test
    public void test() throws ConfigurationException, MalformedURLException {
        KoodistoConfiguration koodistoConfiguration = new KoodistoConfiguration();
        assertEquals("http://koodistopalvelu.opintopolku.fi/puuppa", koodistoConfiguration.getKoodistoResourceUri("puuppa"));
        assertEquals("http://koodistopalvelu.opintopolku.fi/puuppa/koodi/paappu",
                koodistoConfiguration.getKoodiResourceUri("puuppa", "paappu"));
    }
}
