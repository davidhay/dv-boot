package org.datavaultplatform.common.event.deposit;

import javax.persistence.Entity;
import org.datavaultplatform.common.event.Event;

@Entity
public class TransferComplete extends Event {
    
    TransferComplete() {};
    public TransferComplete(String jobId, String depositId) {
        super("File transfer completed");
        this.eventClass = TransferComplete.class.getCanonicalName();
        this.depositId = depositId;
        this.jobId = jobId;
    }
}
