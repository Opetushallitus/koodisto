package fi.vm.sade.koodisto.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("/koodisto-service");
        return new OpenAPI().servers(List.of(server)).info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Koodisto API")
                .description("Koodiston tarjoamat rajapinnat");
    }
}
