package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.OidProvider;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.ThreadLocalAuthorizer;
import fi.vm.sade.koodisto.service.conversion.KoodistoConversionService;
import fi.vm.sade.koodisto.service.conversion.impl.KoodistoConversionServiceImpl;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.*;
import fi.vm.sade.koodisto.service.conversion.impl.koodisto.*;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaToInternalKoodistoRyhmaDto;
import fi.vm.sade.koodisto.service.conversion.impl.koodistoryhma.KoodistoRyhmaToKoodistoRyhmaListDtoConverter;
import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OidProvider oidProvider() {
        return new OidProvider();
    }

    @Bean
    public Authorizer authorizer() {
        return new ThreadLocalAuthorizer();
    }

    @Bean
    OrganisationHierarchyAuthorizer hierarchyAuthorizer() {
        return new OrganisationHierarchyAuthorizer();
    }

    @Bean
    public KoodistoConversionService conversionService(OphProperties ophProperties) {
        KoodistoToKoodistoListDtoConverter koodistoToKoodistoListDtoConverter = new KoodistoToKoodistoListDtoConverter(ophProperties, new KoodistoVersioToKoodistoVersioListDtoConverter());
        KoodistoConversionServiceImpl ks = new KoodistoConversionServiceImpl();
        ks.addConverter(koodistoToKoodistoListDtoConverter);
        ks.addConverter(new KoodiMetadataDtoToKoodiMetadataTypeConverter());
        ks.addConverter(new KoodistoMetadataToKoodistoMetadataTypeConverter());
        ks.addConverter(new KoodistoVersioToKoodistoDtoConverter(
                ophProperties));
        ks.addConverter(new KoodistoTypeToKoodistoVersioConverter());
        ks.addConverter(new KoodiTypeToKoodiVersioConverter());
        ks.addConverter(new KoodiMetadataTypeToKoodiMetadataConverter());
        ks.addConverter(new KoodistoMetadataTypeToKoodistoMetadataConverter());
        ks.addConverter(new KoodistoVersioToKoodistoTypeConverter(
                ophProperties));
        ks.addConverter(new KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter());
        ks.addConverter(new KoodiVersioWithKoodistoVersioItemsToKoodiTypeConverter(ophProperties));
        ks.addConverter(new KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter(
                ophProperties,
                new KoodiMetadataToKoodiMetadataDtoConverter()));
        ks.addConverter(new KoodiVersioWithKoodistoItemToKoodiDtoConverter(
                ophProperties,
                new KoodiMetadataToKoodiMetadataDtoConverter()));
        ks.addConverter(new KoodistoRyhmaToKoodistoRyhmaListDtoConverter(
                koodistoToKoodistoListDtoConverter));
        ks.addConverter(new KoodistoVersioToKoodistoVersioListDtoConverter());
        ks.addConverter(new KoodistoRyhmaToKoodistoRyhmaDtoConverter());
        ks.addConverter(new KoodistoVersioToInternalKoodistoListDtoConverter(
                new KoodistoMetadataToKoodistoMetadataDtoConverter(),
                new KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter()));
        ks.addConverter(new KoodistoVersioToInternalKoodistoPageDtoConverter(
                ophProperties,
                new KoodistoRyhmaMetadataToKoodistoRyhmaMetadataDtoConverter(),
                new KoodistoMetadataToKoodistoMetadataDtoConverter(),
                new KoodiVersioToInternalKoodiVersioDtoConverter(
                        new KoodiMetadataToKoodiMetadataDtoConverter())));
        ks.addConverter(new KoodistoRyhmaToInternalKoodistoRyhmaDto());
        return ks;
    }
}
