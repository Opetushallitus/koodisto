package fi.vm.sade.koodisto.common.configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.MalformedURLException;
import java.text.MessageFormat;

public class KoodistoConfiguration {
    private Configuration configurationProperties;

    public KoodistoConfiguration() throws ConfigurationException, MalformedURLException {
        configurationProperties = new PropertiesConfiguration(getClass().getClassLoader().getResource(
                "config/config.properties"));
    }

    public String getBaseUri() {
        return configurationProperties.getString("baseUri");
    }

    public String getKoodistoResourceUri(String koodistoUri) {
        return MessageFormat.format(configurationProperties.getString("koodistoUri"), koodistoUri);
    }

    public String getKoodiResourceUri(String koodistoUri, String koodiUri) {
        return MessageFormat.format(configurationProperties.getString("koodiUri"), koodistoUri, koodiUri);
    }

    public String getProperty(String key, Object... args) {
        return MessageFormat.format(configurationProperties.getString(key), args);
    }
}
