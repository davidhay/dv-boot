package org.datavaultplatform.webapp.app.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("database")
@Slf4j
@TestPropertySource(properties = "broker.timeout.ms=2000")
public class RestTemplateTimeoutTest {

  @Autowired
  RestTemplate restTemplate;

  @Test
  void testTimeout() {
      long start = System.currentTimeMillis();
      ResourceAccessException ex = assertThrows(ResourceAccessException.class,() -> restTemplate.getForEntity("http://example.com:9999/resource",String.class));
      long diff = System.currentTimeMillis() - start;
      assertTrue(diff > 2000);
      assertTrue(diff < 2400);
      assertThat(ex.getMessage()).startsWith("I/O error on GET request for \"http://example.com:9999/resource\": Connect to example.com:9999 ");
  }

}
