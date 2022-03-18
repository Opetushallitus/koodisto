package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.OidProvider;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.ThreadLocalAuthorizer;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.conversion.impl.KoodistoConversionServiceImpl;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.*;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.*;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaToKoodistoRyhmaListDtoConverter;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OidProvider oidProvider() {
        return new OidProvider();
    }

    @Bean
    public Authorizer authorizer() { return new ThreadLocalAuthorizer();}

    @Bean
    OrganisationHierarchyAuthorizer hierarchyAuthorizer() { return new OrganisationHierarchyAuthorizer();}

    @Autowired
    OphProperties ophProperties;

    @Bean
    public KoodistoConversionService conversionService() {
        KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter = new KoodistoToKoodistoListDtoConverter(ophProperties, new KoodistoVersioToKoodistoVersioListDtoConverter());
        KoodistoConversionServiceImpl ks =  new KoodistoConversionServiceImpl();
        ks.addConverter(koodistoToKoodistoListDtoConverter);
        ks.addConverter(new KoodiMetadataToKoodiMetadataTypeConverter());
        ks.addConverter(new KoodistoMetadataToKoodistoMetadataTypeConverter());
        ks.addConverter(new KoodistoVersioToKoodistoDtoConverter(ophProperties));
        ks.addConverter(new KoodistoTypeToKoodistoVersioConverter());
        ks.addConverter(new KoodiTypeToKoodiVersioConverter());
        ks.addConverter(new KoodiMetadataTypeToKoodiMetadataConverter());
        ks.addConverter(new KoodistoMetadataTypeToKoodistoMetadataConverter());
        ks.addConverter(new KoodistoVersioToKoodistoTypeConverter(ophProperties));
        ks.addConverter(new KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter());
        ks.addConverter(new KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter(ophProperties));
        ks.addConverter(new KoodiVersioWithKoodistoItemToKoodiDtoConverter(ophProperties));
        ks.addConverter(new KoodistoRyhmaToKoodistoRyhmaListDtoConverter(koodistoToKoodistoListDtoConverter));
        ks.addConverter(new KoodistoVersioToKoodistoVersioListDtoConverter());
        ks.addConverter(new KoodistoRyhmaToKoodistoRyhmaDtoConverter());
        return ks;
    }

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
