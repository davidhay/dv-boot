package org.datavaultplatform.webapp.model;

import java.util.Collection;
import org.datavaultplatform.common.model.PermissionModel;
import org.datavaultplatform.common.model.RoleModel;

public class RoleViewModel {

    private final Collection<PermissionModel> unsetPermissions;
    private final RoleModel role;

    public RoleViewModel(RoleModel role, Collection<PermissionModel> unsetPermissions) {
        this.role = role;
        this.unsetPermissions = unsetPermissions;
    }

    public RoleModel getRole() {
        return role;
    }

    public Collection<PermissionModel> getUnsetPermissions() {
        return unsetPermissions;
    }
}
