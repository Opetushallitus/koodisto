package fi.vm.sade.koodisto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Profile("dev")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = false, prePostEnabled = false, securedEnabled = true)
public class DevWebSecurityConfiguration {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(it -> {})
            .headers(headers -> headers.disable())
            .csrf(csrf -> csrf.disable())
            .securityMatcher("/**")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/health").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("devaaja")
                .password(passwordEncoder().encode("devaaja"))
                .roles("APP_OPPIJANUMEROREKISTERI_REKISTERINPITAJA")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}