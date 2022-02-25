package fi.vm.sade.koodisto.configuration;

import fi.vm.sade.koodisto.service.conversion.impl.koodi.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    /*@Value("${server.swagger.context-path}")
    private String swaggerPath;
*/
    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("forward:/index.html");
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController(("/{spring:\\w+}/**{spring:?!(\\.js|\\.css)$}"))
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }*/

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new KoodiMetadataToKoodiMetadataTypeConverter());
        registry.addConverter(new KoodiMetadataTypeToKoodiMetadataConverter());
        registry.addConverter(new KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter());
        registry.addConverter(new KoodiVersioWithKoodistoItemToKoodiDtoConverter());
        registry.addConverter(new KoodiVersioWithKoodistoItemToSimpleKoodiDtoConverter());
        registry.addConverter(new KoodiVersioWithKoodistoVersioItemsToKoodiDtoConverter());
        registry.addConverter(new KoodiVersioWithKoodistoVersioItemsToKoodiTypeConverter());
    }
}
