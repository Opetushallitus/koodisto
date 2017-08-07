package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class AuditPostUpdateEventListener implements PostUpdateEventListener {

    private final Audit audit;

    public AuditPostUpdateEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Serializable eventId = event.getId();
        Target.Builder targetBuilder = new Target.Builder()
                .setField(targetKey, eventId.toString());

        PostUpdateEventAdapter eventAdapter = new PostUpdateEventAdapter(event);
        Changes.Builder changesBuilder = new Changes.Builder();
        for (int dirtyProperty : event.getDirtyProperties()) {
            AuditUtils.addChange(eventAdapter, dirtyProperty, changesBuilder);
        }

        audit.log(AuditUtils.getUser(), KoodistoOperation.PAIVITYS,
                targetBuilder.build(), changesBuilder.build());
    }

    private static class PostUpdateEventAdapter implements Event {

        private final PostUpdateEvent event;

        public PostUpdateEventAdapter(PostUpdateEvent event) {
            this.event = event;
        }

        @Override
        public EntityPersister getEntityPersister() {
            return event.getPersister();
        }

        @Override
        public SessionImplementor getSessionImplementor() {
            return event.getSession();
        }

        @Override
        public Object getEntity() {
            return event.getEntity();
        }

        @Override
        public Object getOldState(int index) {
            return event.getOldState()[index];
        }

        @Override
        public Object getNewState(int index) {
            return event.getState()[index];
        }

    }

}
