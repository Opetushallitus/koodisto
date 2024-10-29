package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource("classpath:application.properties")
@Configuration
public class UrlConfiguration extends OphProperties {
    public UrlConfiguration(Environment environment) {
        this.addDefault("host.alb", environment.getRequiredProperty("host.alb"));

        this.addDefault("cas.base", environment.getRequiredProperty("cas.base"));
        this.addDefault("cas.login", environment.getRequiredProperty("cas.login"));

        this.addOverride("koodistoUriFormat", environment.getRequiredProperty("koodistoUriFormat"));
        this.addOverride("koodiUriFormat", environment.getRequiredProperty("koodiUriFormat"));
    }
}
