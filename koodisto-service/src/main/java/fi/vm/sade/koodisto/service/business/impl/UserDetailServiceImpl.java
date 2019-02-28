package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.service.business.UserDetailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailService {
    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public String getCurrentUserOid() {
        return getAuthentication()
                .map(authentication -> Optional.ofNullable(authentication.getName())
                        .orElseThrow(() -> new IllegalStateException("Käyttäjällä ei ole oidia")))
                .orElseThrow(() -> new IllegalStateException("Käyttäjä ei ole kirjautunut"));
    }

    public static Optional<String> findCurrentUserOid() {
        return getAuthentication()
                .flatMap(authentication -> Optional.ofNullable(authentication.getName()));
    }

}
