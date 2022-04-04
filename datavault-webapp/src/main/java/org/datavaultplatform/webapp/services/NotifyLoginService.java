package org.datavaultplatform.webapp.services;

import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.request.CreateClientEvent;

public interface NotifyLoginService {

  String notifyLogin(CreateClientEvent clientEvent);

  Group[] getGroups();
}
