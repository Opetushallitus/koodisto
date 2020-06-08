package fi.vm.sade.koodisto;

import fi.vm.sade.koodisto.service.config.PostgreSqlConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:META-INF/spring/bundle-context.xml")
@EnableConfigurationProperties(PostgreSqlConfigurationProperties.class)
public class KoodistoApplication {
    public static void main(String[] args) {
        SpringApplication.run(KoodistoApplication.class, args);
    }
}
