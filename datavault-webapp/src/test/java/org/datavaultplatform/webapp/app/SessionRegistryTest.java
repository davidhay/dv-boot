package org.datavaultplatform.webapp.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AddTestProperties
@AutoConfigureMockMvc
public class SessionRegistryTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  @Qualifier("sessionRegistry")
  SessionRegistry sessionRegistry;

  @Test
  @WithMockUser
  void testSessionRegistryBean() {
    assertNotNull(sessionRegistry);
    assertThat(sessionRegistry.getAllPrincipals()).isEmpty();
  }

}
