package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.User;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.hibernate.persister.entity.EntityPersister;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuditUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditUtils.class);

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
    public static Oid getOid() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        String oid = authentication.getName();
        try {
            return new Oid(oid);
        } catch (GSSException ex) {
            LOGGER.error("Käyttäjän OID '{}' ei ole oikeassa muodossa", oid, ex);
        }
        return null;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getUser() {
        Oid oid = getOid();
        String remoteAddr = MDC.get("remoteAddr");
        String session = MDC.get("session");
        String userAgent = MDC.get("userAgent");
        return new User(oid, createInetAddress(remoteAddr), session, userAgent);
    }

    private static InetAddress createInetAddress(String remoteAddr) {
        try {
            return InetAddress.getByName(remoteAddr);
        } catch (UnknownHostException e1) {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

}
