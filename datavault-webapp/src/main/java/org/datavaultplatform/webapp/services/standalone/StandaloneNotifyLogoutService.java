package org.datavaultplatform.webapp.services.standalone;

import org.datavaultplatform.common.request.CreateClientEvent;
import org.datavaultplatform.webapp.services.NotifyLogoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("standalone")
@Service
public class StandaloneNotifyLogoutService implements NotifyLogoutService {

  Logger LOG = LoggerFactory.getLogger(StandaloneNotifyLogoutService.class);

  @Override
  public String notifyLogout(CreateClientEvent clientEvent) {
    LOG.info("notifyLogoutService {}", clientEvent);
    return "XXX";
  }

}
