package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class AuditPostDeleteEventListener implements PostDeleteEventListener {

    private final Audit audit;

    public AuditPostDeleteEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Object targetId = event.getId();
        Target.Builder targetBuilder = new Target.Builder()
                .setField(targetKey, targetId.toString());

        PostDeleteEventAdapter eventAdapter = new PostDeleteEventAdapter(event);
        Changes.Builder changesBuilder = new Changes.Builder();
        for (int i = 0; i < event.getDeletedState().length; ++i) {
            AuditUtils.addChange(eventAdapter, i, changesBuilder);
        }

        audit.log(AuditUtils.getUser(), KoodistoOperation.POISTO,
                targetBuilder.build(), changesBuilder.build());
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    private static class PostDeleteEventAdapter implements Event {

        private final PostDeleteEvent event;

        public PostDeleteEventAdapter(PostDeleteEvent event) {
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
            return event.getDeletedState()[index];
        }

        @Override
        public Object getNewState(int index) {
            return null;
        }

    }

}
