package fi.vm.sade.koodisto.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cas")
public class CasProperties {

    private String service;
    private Boolean sendRenew;
    private String key;
}