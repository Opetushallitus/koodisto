package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.javautils.kayttooikeusclient.OphUserDetailsServiceImpl;
import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

@Profile("!dev")
@Configuration
public class UserDetailsServiceConfiguration {

    private final OphProperties ophProperties;

    public UserDetailsServiceConfiguration(OphProperties ophProperties) {
        this.ophProperties = ophProperties;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        String host = ophProperties.url("host.alb");
        return new OphUserDetailsServiceImpl(host, "1.2.246.562.10.00000000001.koodisto-service");
    }

}
