package org.datavaultplatform.webapp.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@SpringBootTest
public class SessionRegistryTest {

  @Autowired
  @Qualifier("sessionRegistry")
  SessionRegistry sessionRegistry;

  @Test
  void testSessionRegistryBean() {
    //TODO - we need to check whether a new login adds a session to registry
    Logger regLogger = LoggerFactory.getLogger(SessionRegistryImpl.class);
    regLogger.debug("THIS IS A LOGGING TEST");
    assertNotNull(sessionRegistry);
  }

}
