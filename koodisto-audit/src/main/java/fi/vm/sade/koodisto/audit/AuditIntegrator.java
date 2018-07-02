package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Auditlokin konfiguraatio.
 *
 * Aktivointi:
 * src/main/resources/META-INF/services/org.hibernate.integrator.spi.Integrator
 */
public class AuditIntegrator implements Integrator {

    private final Audit audit;

    public AuditIntegrator() {
        this.audit = new Audit(new AuditLogger(), "koodisto", ApplicationType.BACKEND);
    }

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, new AuditPostInsertEventListener(audit));
        eventListenerRegistry.appendListeners(EventType.POST_UPDATE, new AuditPostUpdateEventListener(audit));
        eventListenerRegistry.appendListeners(EventType.POST_DELETE, new AuditPostDeleteEventListener(audit));
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // nop
    }

}
