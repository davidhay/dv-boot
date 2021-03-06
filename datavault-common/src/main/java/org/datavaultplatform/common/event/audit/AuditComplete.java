package org.datavaultplatform.common.event.audit;

import javax.persistence.Entity;
import org.datavaultplatform.common.event.Event;

@Entity
public class AuditComplete extends Event{
    AuditComplete() {};
    public AuditComplete(String jobId, String auditId) {
        super(jobId, auditId, null, null, null, "Audit completed");
        this.eventClass = AuditComplete.class.getCanonicalName();
    }
}
