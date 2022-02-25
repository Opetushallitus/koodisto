package fi.vm.sade.koodisto.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public abstract class userData {
    public static Optional<String> getCurrentUserOid() { return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());}
}
