package fi.vm.sade.koodisto.configuration;

import org.springframework.boot.servlet.filter.OrderedFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    // vanhaa versiota varten ohajaan kyselyt buildversion.txt:stä actuator endpointiin.
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/buildversion.txt")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/actuator/health")
                .setViewName("forward:/actuator/health");
        registry.addViewController("/ui/**")
                .setViewName("forward:/index.html");
        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }

    @Bean
    public FilterRegistrationBean<UrlHandlerFilter> trailingSlashHandlerFilter() {
        var registration = new FilterRegistrationBean<>(
                UrlHandlerFilter.trailingSlashHandler("/**").wrapRequest().build());
        registration.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER);
        return registration;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("public/static/**").addResourceLocations("/static/");
    }
}
