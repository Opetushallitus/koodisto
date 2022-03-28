package fi.vm.sade.koodisto.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public interface userData {
    static Optional<String> getCurrentUserOid() { return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());}
}
