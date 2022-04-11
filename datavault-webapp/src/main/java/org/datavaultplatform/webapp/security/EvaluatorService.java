package org.datavaultplatform.webapp.security;

import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.model.Vault;
import org.datavaultplatform.common.response.VaultInfo;

public interface EvaluatorService {

  VaultInfo getVault(String id);

  Group getGroup(String groupID);

  Vault getVaultRecord(String id);
}
