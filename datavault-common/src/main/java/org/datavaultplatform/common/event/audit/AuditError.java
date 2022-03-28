package org.datavaultplatform.common.event.audit;

import javax.persistence.Entity;
import org.datavaultplatform.common.event.Event;

@Entity
public class AuditError extends Event {

    AuditError(){}
    public AuditError(String jobId, String auditId, String chunkId, String archiveId, String location) {
        super(jobId, auditId, chunkId, archiveId, location, "Error while Auditing: "+chunkId);
        this.eventClass = AuditError.class.getCanonicalName();
    }

    public AuditError(String jobId, String auditId, String chunkId, String archiveId, String location, String message) {
        super(jobId, auditId, chunkId, archiveId, location, message);
        this.eventClass = AuditError.class.getCanonicalName();
    }
}
