package org.datavaultplatform.webapp.app.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.datavaultplatform.webapp.test.DummyNotifyLogoutServiceConfig;
import org.datavaultplatform.webapp.test.TestUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

/**
 * Checks that when a user logs in, their session is registered in the SessionRegistry
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AddTestProperties
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "datavault.csrf.disabled=true")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Import(DummyNotifyLogoutServiceConfig.class)
public class SessionRegistryUsageTest {

  @Value("${spring.security.user.name}")
  String username;

  @Value("${spring.security.user.password}")
  String password;

  @Autowired
  SessionRegistry sessionRegistry;

  @Autowired
  TestRestTemplate template;

  @Test
  void testSessionRegistrationOnSuccessfulLogin() {

    ResponseEntity<String> responseEntity = TestUtils.login(template, username, password);

    String sessionId = TestUtils.getSessionId(responseEntity.getHeaders());

    Callable<Boolean> ready = () -> sessionRegistry.getAllPrincipals().isEmpty() == false;

    await().atMost(5, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .until(ready);

    assertThat(sessionRegistry.getAllPrincipals()).hasSize(1);
    User principal = (User) sessionRegistry.getAllPrincipals().get(0);
    assertThat(principal.getUsername()).isEqualTo(username);

    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, true);
    assertThat(sessions).hasSize(1);
    SessionInformation registeredSession = sessions.get(0);

    assertThat(registeredSession.getSessionId()).isEqualTo(sessionId);
    assertThat(registeredSession.isExpired()).isFalse();
  }

}
