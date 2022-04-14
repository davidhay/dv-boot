package org.datavaultplatform.webapp.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("standalone")
public class PropertiesTest {

  @Test
  void testApplicationProperties(@Value("${spring.application.name}") String appName) {
    assertEquals("datavault-webapp", appName);
  }

  @Test
  void testDataVaultProperties(@Value("${webapp.logout.url}") String logoutUrl) {
    assertEquals("/auth/confirmation", logoutUrl);
  }

  @Test
  void testDisplayName(@Value("${server.servlet.application-display-name}") String displayName){
    assertEquals("datavault-webapp", displayName);
  }

}
