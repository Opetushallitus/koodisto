package fi.vm.sade.koodisto.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    /*@Value("${server.swagger.context-path}")
    private String swaggerPath;
*/
    // vanhaa versiota varten ohajaan kyselyt buildversion.txt:stä actuator endpointiin.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/buildversion.txt")
                .setViewName("forward:/actuator/health");
    }

    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }*/


}
