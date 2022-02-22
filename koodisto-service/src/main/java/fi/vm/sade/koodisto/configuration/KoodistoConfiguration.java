package fi.vm.sade.koodisto.configuration;

import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.text.MessageFormat;

@Configuration
public class KoodistoConfiguration {
    static {
        System.setProperty("fi.vm.sade.javautils.http.HttpServletRequestUtils.HARMLESS_URLS", "/koodisto-service/buildversion.txt");
    }
    protected Configuration configurationProperties;

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
