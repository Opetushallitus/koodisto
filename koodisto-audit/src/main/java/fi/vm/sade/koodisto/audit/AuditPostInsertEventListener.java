package fi.vm.sade.koodisto.audit;

import fi.vm.sade.auditlog.Audit;
import java.io.Serializable;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

public class AuditPostInsertEventListener implements PostInsertEventListener {

    private final Audit audit;

    public AuditPostInsertEventListener(Audit audit) {
        this.audit = audit;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        String targetKey = AuditUtils.getTargetKey(event.getPersister());
        Serializable targetId = event.getId();
        String oid = AuditUtils.getOid();

        LogMessage.LogMessageBuilder builder = LogMessage.builder()
                .operation(KoodistoOperation.LISAYS)
                .target(targetKey, targetId.toString())
                .id(oid);
        audit.log(builder.build());
    }

}
