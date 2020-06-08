package fi.vm.sade.koodisto.service.config;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.OidProvider;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.javautils.opintopolku_spring_security.ThreadLocalAuthorizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class AuthorizerConfiguration {

    @Bean
    public Authorizer authorizer() {
        return new ThreadLocalAuthorizer();
    }

    @Bean
    public OrganisationHierarchyAuthorizer organisationHierarchyAuthorizer(OidProvider oidProvider) {
        return new OrganisationHierarchyAuthorizer(oidProvider);
    }

}
