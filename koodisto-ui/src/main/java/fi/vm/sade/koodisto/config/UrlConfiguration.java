package fi.vm.sade.koodisto.config;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@PropertySource("file:///${user.home:''}/oph-configuration/koodisto-ui.properties")
public class UrlConfiguration extends OphProperties {
    private Environment environment;

    public UrlConfiguration(Environment environment) {
        this.environment = environment;

        this.setOptionalProperty("organisaatio-service.hae");
        this.setOptionalProperty("organisaatio-service.parentoids");
        this.setOptionalProperty("koodisto-service.base");
        this.setOptionalProperty("koodisto-service.codesgroup");
        this.setOptionalProperty("organisaatio-service.byOid");
        this.setOptionalProperty("cas.myroles");
        this.setOptionalProperty("koodisto-service.i18n");
    }

    private void setOptionalProperty(String propertyKey) {
        String propertyValue = this.environment.getProperty(propertyKey);
        if (StringUtils.hasLength(propertyValue)) {
            this.frontProperties.setProperty(propertyKey, propertyValue);
        }
    }
}
