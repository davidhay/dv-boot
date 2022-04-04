package org.datavaultplatform.webapp.test;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.request.CreateClientEvent;
import org.datavaultplatform.webapp.services.NotifyLoginService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@Slf4j
public class DummyNotifyLoginServiceConfig {

  @Bean
  @Primary
  NotifyLoginService dummyNotifyLoginService() {
    return new NotifyLoginService() {

      @Override
      public String notifyLogin(CreateClientEvent clientEvent) {
        log.info("notifyLogin {}", clientEvent);
        return "NOTIFIED";
      }

      @Override
      public Group[] getGroups() {
        return new Group[0];
      }
    };
  }
}
