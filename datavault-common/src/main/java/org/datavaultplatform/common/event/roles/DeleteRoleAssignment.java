package org.datavaultplatform.common.event.roles;

import javax.persistence.Entity;
import org.datavaultplatform.common.event.Event;
import org.datavaultplatform.common.model.RoleAssignment;

@Entity
public class DeleteRoleAssignment extends Event {

    DeleteRoleAssignment() {};
    public DeleteRoleAssignment(RoleAssignment roleAssignment, String userId) {
        super(roleAssignment.getRole().getName()+" role removed from "+roleAssignment.getUserId()+" by "+userId);
        this.eventClass = DeleteRoleAssignment.class.getCanonicalName();
    }
}
