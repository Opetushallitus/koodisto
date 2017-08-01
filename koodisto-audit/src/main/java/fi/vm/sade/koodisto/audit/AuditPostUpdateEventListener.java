package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        EntityPersister persister = event.getPersister();
        Map<String, Change> changes = new HashMap<>();

        int[] dirtyProperties = event.getDirtyProperties();
        for (int dirtyProperty : dirtyProperties) {
            Type propertyType = persister.getPropertyTypes()[dirtyProperty];
            if (propertyType.isAssociationType() || propertyType.isCollectionType()) {
                continue;
            }
            Object oldValue = event.getOldState()[dirtyProperty];
            Object newValue = event.getState()[dirtyProperty];
            if (!Objects.equals(oldValue, newValue)) {
                String propertyName = persister.getPropertyNames()[dirtyProperty];
                changes.put(propertyName, Change.of(oldValue, newValue));
            }
        }

        if (!changes.isEmpty()) {
            String targetKey = AuditUtils.getTargetKey(persister);
            Serializable eventId = event.getId();
            String oid = AuditUtils.getOid();

            LogMessage.LogMessageBuilder builder = LogMessage.builder()
                    .operation(KoodistoOperation.PAIVITYS)
                    .target(targetKey, eventId.toString())
                    .id(oid)
                    .changesJson(changes);
            audit.log(builder.build());
        }
    }

    /**
     * Lokisisällössä käytettävä formaatti muutoksille.
     */
    private static class Change {

        private final Object oldValue;
        private final Object newValue;

        private Change(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public static Change of(Object oldValue, Object newValue) {
            return new Change(oldValue, newValue);
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public String toString() {
            return "Change{" + "oldValue=" + oldValue + ", newValue=" + newValue + '}';
        }

    }

}
