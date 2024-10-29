package fi.vm.sade.koodisto.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.RequiredArgsConstructor;

import static fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD;

@Profile("dev")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = false, prePostEnabled = false, securedEnabled = true)
@RequiredArgsConstructor
public class DevWebSecurityConfiguration {
    private static final String RESTRICTED = "restricted";
    private static final String DEVAAJA = "devaaja";
    private static final String OID_USERNAME = "1.2.3.4.5";
    private static final SimpleGrantedAuthority[] OPH_AUTHORITIES = new SimpleGrantedAuthority[]{
            new SimpleGrantedAuthority(String.format("%s_1.2.246.562.10.00000000001", ROLE_APP_KOODISTO_CRUD)),
            new SimpleGrantedAuthority(ROLE_APP_KOODISTO_CRUD)
    };
    private static final SimpleGrantedAuthority[] RESTRICTED_AUTHORITIES = new SimpleGrantedAuthority[]{
            new SimpleGrantedAuthority("ROLE_APP_KOODISTO_READ")
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(it -> {})
            .headers(headers -> headers.disable())
            .csrf(csrf -> csrf.disable())
            .securityMatcher("/**")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/buildversion.txt").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/rest/**").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        var passwordEncoder = passwordEncoder();
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (RESTRICTED.equals(username)) {
                    return User.builder()
                            .authorities(List.of(RESTRICTED_AUTHORITIES))
                            .password(passwordEncoder.encode(username))
                            .username(username)
                            .build();
                } else if (DEVAAJA.equals(username)) {
                    return User.builder()
                            .authorities(List.of(OPH_AUTHORITIES))
                            .password(passwordEncoder.encode(DEVAAJA))
                            .username(DEVAAJA)
                            .build();
                } else {
                    return User.builder()
                            .authorities(List.of(OPH_AUTHORITIES))
                            .password(passwordEncoder.encode(DEVAAJA))
                            .username(OID_USERNAME)
                            .build();
                }
            }
        };
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}