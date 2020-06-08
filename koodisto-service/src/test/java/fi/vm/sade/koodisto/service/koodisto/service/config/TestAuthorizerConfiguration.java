package fi.vm.sade.koodisto.service.koodisto.service.config;

import fi.vm.sade.javautils.opintopolku_spring_security.Authorizer;
import fi.vm.sade.koodisto.service.business.MockAuthorizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestAuthorizerConfiguration {

    @Bean
    public Authorizer authorizer() {
        return new MockAuthorizer();
    }

}
