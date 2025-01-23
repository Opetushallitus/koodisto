package fi.vm.sade.koodisto.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    // vanhaa versiota varten ohajaan kyselyt buildversion.txt:stä actuator endpointiin.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/buildversion.txt")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/actuator/health")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/ui")
                .setViewName("forward:/index.html");
        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
      configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("public/static/**").addResourceLocations("/static/");
    }
}
