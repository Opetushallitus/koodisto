package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.io.Serializable;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.type.Type;

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
        Changes.Builder changesBuilder = new Changes.Builder();
        for (int i = 0; i < event.getState().length; ++i) {
            Type propertyType = event.getPersister().getPropertyTypes()[i];
            if (propertyType.isAssociationType() || propertyType.isCollectionType()) {
                continue;
            }
            Object value = event.getState()[i];
            if (value != null) {
                String propertyName = event.getPersister().getPropertyNames()[i];
                changesBuilder.added(propertyName, value.toString());
            }
        }

        audit.log(AuditUtils.getUser(), KoodistoOperation.LISAYS,
                targetBuilder.build(), changesBuilder.build());
    }

}
