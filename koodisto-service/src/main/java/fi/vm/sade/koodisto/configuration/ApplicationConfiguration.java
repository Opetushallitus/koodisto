package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.OidProvider;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.ThreadLocalAuthorizer;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.conversion.impl.KoodistoConversionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OidProvider oidProvider() {
        return new OidProvider();
    }

    @Bean
    public Authorizer authorizer() { return new ThreadLocalAuthorizer();} // TODO onko oikein?

    @Bean
    OrganisationHierarchyAuthorizer hierarchyAuthorizer() { return new OrganisationHierarchyAuthorizer();}

    @Bean
    public KoodistoConversionService conversionService() { return new KoodistoConversionServiceImpl(); }

    /*static {
        System.setProperty("fi.vm.sade.javautils.http.HttpServletRequestUtils.HARMLESS_URLS", "/koodisto-service/buildversion.txt");
    }
    protected Configuration configurationPr operties;

    public ApplicationConfiguration() throws ConfigurationException, MalformedURLException {
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

     */
}
