package fi.vm.sade.koodisto.config;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

import static java.util.Optional.ofNullable;

@Configuration
public class UrlConfiguration extends OphProperties {
    public UrlConfiguration() {
        addFiles("/koodisto-service-oph.properties");
        if (!ofNullable(System.getProperty("spring.profiles.active")).orElse("").contains("test")) {
            addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        }
    }
}
