package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.OidProvider;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.ThreadLocalAuthorizer;
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


}
