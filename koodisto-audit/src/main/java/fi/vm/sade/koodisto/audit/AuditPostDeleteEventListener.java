package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import java.io.Serializable;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;

public class AuditPostDeleteEventListener implements PostDeleteEventListener {

    private final Audit audit;

    public AuditPostDeleteEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Serializable targetId = event.getId();
        String oid = AuditUtils.getOid();

        LogMessage.LogMessageBuilder builder = LogMessage.builder()
                .operation(KoodistoOperation.POISTO)
                .target(targetKey, targetId.toString())
                .id(oid);
        audit.log(builder.build());
    }

}
