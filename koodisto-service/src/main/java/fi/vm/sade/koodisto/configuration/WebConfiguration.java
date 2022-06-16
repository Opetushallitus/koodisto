package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.properties.OphProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final OphProperties ophProperties;

    // vanhaa versiota varten ohajaan kyselyt buildversion.txt:stä actuator endpointiin.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/buildversion.txt")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/")
                .setViewName("forward:/actuator/health");
    }

}
