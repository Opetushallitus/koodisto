package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource("classpath:application.properties")
@Configuration
public class UrlConfiguration extends OphProperties {

    @Autowired
    public UrlConfiguration(Environment environment) {
        this.addDefault("host.alb", environment.getRequiredProperty("host.alb"));

        this.addDefault("cas.base", environment.getRequiredProperty("cas.base"));
        this.addDefault("cas.login", environment.getRequiredProperty("cas.login"));

        this.addOverride("koodistoUri", environment.getRequiredProperty("koodistoUri"));
        this.addOverride("koodiUri", environment.getRequiredProperty("koodiUri"));
    }
}
