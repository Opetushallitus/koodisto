package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public class AuditPostUpdateEventListener implements PostUpdateEventListener {

    private final Audit audit;

    public AuditPostUpdateEventListener(Audit audit) {
        this.audit = audit;
    }

    private static String toString(Object object) {
        return object != null ? object.toString() : null;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        EntityPersister persister = event.getPersister();
        Changes.Builder changesBuilder = new Changes.Builder();

        int[] dirtyProperties = event.getDirtyProperties();
        for (int dirtyProperty : dirtyProperties) {
            Type propertyType = persister.getPropertyTypes()[dirtyProperty];
            if (propertyType.isAssociationType() || propertyType.isCollectionType()) {
                continue;
            }
            String propertyName = persister.getPropertyNames()[dirtyProperty];
            Object oldValue = event.getOldState()[dirtyProperty];
            Object newValue = event.getState()[dirtyProperty];
            if (oldValue == null && newValue != null) {
                changesBuilder.added(propertyName, newValue.toString());
            } else if (oldValue != null && newValue == null) {
                changesBuilder.removed(propertyName, oldValue.toString());
            } else if (!Objects.equals(oldValue, newValue)) {
                changesBuilder.updated(propertyName, toString(oldValue), toString(newValue));
            }
        }

        String targetKey = AuditUtils.getTargetKey(persister);
        Serializable eventId = event.getId();
        Target.Builder targetBuilder = new Target.Builder()
                .setField(targetKey, eventId.toString());

        audit.log(AuditUtils.getUser(), KoodistoOperation.PAIVITYS,
                targetBuilder.build(), changesBuilder.build());
    }

}
