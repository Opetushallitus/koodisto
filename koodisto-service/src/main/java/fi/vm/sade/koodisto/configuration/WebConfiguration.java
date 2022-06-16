package fi.vm.sade.koodisto.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    // vanhaa versiota varten ohajaan kyselyt buildversion.txt:stä actuator endpointiin.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/buildversion.txt")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/")
                .setViewName("forward:/actuator/health");
    }

}
