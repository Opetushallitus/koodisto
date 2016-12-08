package fi.vm.sade.koodisto.config;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditloggerConfiguration {

    @Bean
    public Audit audit() {
        return new Audit("koodisto", ApplicationType.BACKEND);
    }

}
