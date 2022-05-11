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

import static fi.vm.sade.koodisto.util.KoodistoRole.ROLE_APP_KOODISTO_CRUD;

@Profile("dev")
@Configuration
public class DevUserDetailsServiceConfiguration {
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
            if (RESTRICTED.equals(username)) {
                return User.builder()
                        .authorities(List.of(RESTRICTED_AUTHORITIES))
                        .password(this.passwordEncoder.encode(username))
                        .username(username)
                        .build();
            } else if (DEVAAJA.equals(username)) {
                return User.builder()
                        .authorities(List.of(OPH_AUTHORITIES))
                        .password(this.passwordEncoder.encode(DEVAAJA))
                        .username(DEVAAJA)
                        .build();
            } else {
                return User.builder()
                        .authorities(List.of(OPH_AUTHORITIES))
                        .password(this.passwordEncoder.encode(DEVAAJA))
                        .username(OID_USERNAME)
                        .build();
            }
        }
    }
}
