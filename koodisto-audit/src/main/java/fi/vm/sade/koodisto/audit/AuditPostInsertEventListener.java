package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class AuditPostInsertEventListener implements PostInsertEventListener {

    private final Audit audit;

    public AuditPostInsertEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Serializable targetId = event.getId();
        Target.Builder targetBuilder = new Target.Builder()
                .setField(targetKey, targetId.toString());

        PostInsertEventAdapter eventAdapter = new PostInsertEventAdapter(event);
        Changes.Builder changesBuilder = new Changes.Builder();
        for (int i = 0; i < event.getState().length; ++i) {
            AuditUtils.addChange(eventAdapter, i, changesBuilder);
        }

        audit.log(AuditUtils.getUser(), KoodistoOperation.LISAYS,
                targetBuilder.build(), changesBuilder.build());
    }

    private static class PostInsertEventAdapter implements Event {

        private final PostInsertEvent event;

        public PostInsertEventAdapter(PostInsertEvent event) {
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
            return null;
        }

        @Override
        public Object getNewState(int index) {
            return event.getState()[index];
        }

    }

}
