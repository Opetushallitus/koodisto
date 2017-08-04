package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import java.io.Serializable;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.type.Type;

public class AuditPostDeleteEventListener implements PostDeleteEventListener {

    private final Audit audit;

    public AuditPostDeleteEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Serializable targetId = event.getId();
        Target.Builder targetBuilder = new Target.Builder()
                .setField(targetKey, targetId.toString());
        Changes.Builder changesBuilder = new Changes.Builder();
        for (int i = 0; i < event.getDeletedState().length; ++i) {
            Type propertyType = event.getPersister().getPropertyTypes()[i];
            if (propertyType.isAssociationType() || propertyType.isCollectionType()) {
                continue;
            }
            Object value = event.getDeletedState()[i];
            if (value != null) {
                String propertyName = event.getPersister().getPropertyNames()[i];
                changesBuilder.removed(propertyName, value.toString());
            }
        }

        audit.log(AuditUtils.getUser(), KoodistoOperation.POISTO,
                targetBuilder.build(), changesBuilder.build());
    }

}
