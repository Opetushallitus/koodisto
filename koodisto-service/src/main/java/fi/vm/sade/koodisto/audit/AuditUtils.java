package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.User;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class AuditUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditUtils.class);

    private AuditUtils() {
    }

    public static void addChange(Event event, int index, Changes.Builder changesBuilder) {
        EntityPersister entityPersister = event.getEntityPersister();
        Type propertyType = entityPersister.getPropertyTypes()[index];
        if (propertyType.isCollectionType()) {
            return;
        }
        if (propertyType.isAssociationType()) {
            Object oldState = event.getOldState(index);
            Object oldStateId = oldState != null
                    ? entityPersister.getIdentifier(oldState, event.getSessionImplementor())
                    : null;
            Object newState = event.getNewState(index);
            Object newStateId = newState != null
                    ? entityPersister.getIdentifier(newState, event.getSessionImplementor())
                    : null;

            String propertyName = entityPersister.getPropertyNames()[index];
            addChange(propertyName, oldStateId, newStateId, changesBuilder);
        } else {
            Object oldState = event.getOldState(index);
            Object newState = event.getNewState(index);
            String propertyName = entityPersister.getPropertyNames()[index];
            addChange(propertyName, oldState, newState, changesBuilder);
        }
    }

    private static void addChange(String propertyName, Object oldState, Object newState, Changes.Builder changesBuilder) {
        if (oldState == null && newState != null) {
            changesBuilder.added(propertyName, newState.toString());
        } else if (oldState != null && newState == null) {
            changesBuilder.removed(propertyName, oldState.toString());
        } else if (oldState != null && newState != null && !oldState.equals(newState)) {
            changesBuilder.updated(propertyName, oldState.toString(), newState.toString());
        }
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

    private static boolean isOauth2User(Authentication auth) {
        return auth != null && auth.getName() != null && !auth.getName().startsWith("1.");
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getUser() {
        String remoteAddr = MDC.get("remoteAddr");
        String session = MDC.get("session");
        String userAgent = MDC.get("userAgent");

        Authentication authentication = getAuthentication();
        if (isOauth2User(authentication)) {
            return new Oauth2User(authentication.getName(), createInetAddress(remoteAddr), userAgent);
        }
        return new User(getOid(), createInetAddress(remoteAddr), session, userAgent);
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
