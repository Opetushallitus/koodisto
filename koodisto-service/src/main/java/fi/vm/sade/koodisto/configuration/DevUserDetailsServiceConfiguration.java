package fi.vm.sade.koodisto.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Profile("dev")
@Configuration
public class DevUserDetailsServiceConfiguration {
    private static final SimpleGrantedAuthority[] OPH_AUTHORITIES = new SimpleGrantedAuthority[]{
            new SimpleGrantedAuthority("ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001"),
            new SimpleGrantedAuthority("ROLE_APP_KOODISTO"),
            new SimpleGrantedAuthority("ROLE_APP_KOODISTO_CRUD")
    };
    private static final SimpleGrantedAuthority[] RESTRICTED_AUTHORITIES = new SimpleGrantedAuthority[]{
            new SimpleGrantedAuthority("ROLE_APP_KOODISTO_READ")
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new DevUserDetailsService(passwordEncoder());
    }

    static class DevUserDetailsService implements UserDetailsService {
        PasswordEncoder passwordEncoder;

        private DevUserDetailsService(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if ("restricted".equals(username)) {
                return User.builder()
                        .authorities(List.of(RESTRICTED_AUTHORITIES))
                        .password(this.passwordEncoder.encode(username))
                        .username(username)
                        .build();
            }
            return User.builder()
                    .authorities(List.of(OPH_AUTHORITIES))
                    .password(this.passwordEncoder.encode("devaaja"))
                    .username("devaaja")
                    .build();
        }
    }
}
