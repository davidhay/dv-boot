package org.datavaultplatform.webapp.test;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.services.NotifyLogoutService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@Slf4j
public class DummyNotifyLogoutServiceConfig {

  @Bean
  @Primary
  NotifyLogoutService dummyNotifyLogoutService() {
    return clientEvent -> {
      log.info("Dummy Notify Logout {}", clientEvent);
      return "NOTIFIED";
    };
  }
}
