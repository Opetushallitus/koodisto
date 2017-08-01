package fi.vm.sade.koodisto.audit;

import org.hibernate.persister.entity.EntityPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuditUtils {

    private AuditUtils() {
    }

    /**
     * Palauttaa lokituksessa käytettävän target-kentän avaimen.
     *
     * @param entityPersister entity persister
     * @return target-kentän avain
     */
    public static String getTargetKey(EntityPersister entityPersister) {
        String entityName = entityPersister.getEntityName();
        String entityClass = entityName.substring("fi.vm.sade.koodisto.model.".length());
        return entityClass.toLowerCase();
    }

    /**
     * Palauttaa kirjautuneen käyttäjän OID:n (tai NULL jos ei kirjauduttu)
     *
     * @return oid tai NULL
     */
    public static String getOid() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
