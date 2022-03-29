package org.datavaultplatform.webapp.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.session.HttpSessionCreatedEvent;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AddTestProperties
public class HttpSessionEventPublisherTest {

  @Autowired
  TestRestTemplate template;

  @Autowired
  List<HttpSessionCreatedEvent> events;

  @Autowired
  CountDownLatch latch;

  @Test
  void testSessionCreated() throws InterruptedException {
    ResponseEntity<String> response = template.getForEntity("/test/hello", String.class);
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    String expectedSessionId = getSessionId(response.getHeaders());

    Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    assertThat(events).hasSize(1);
    HttpSessionCreatedEvent event = events.get(0);
    String actualCreatedSessionId = event.getSession().getId();
    assertEquals(expectedSessionId, actualCreatedSessionId);
  }

  private String getSessionId(HttpHeaders headers){
    String setCookie = headers.get("Set-Cookie").get(0);
    StringTokenizer parts = new StringTokenizer(setCookie, ";", false);
    String part1 = parts.nextToken();
    StringTokenizer parts2 = new StringTokenizer(part1, "=", false);
    String jsession=parts2.nextToken();
    String sessionId = parts2.nextToken();
    return sessionId;
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    ArrayList<HttpSessionCreatedEvent> events(){
        return new ArrayList<>();
    }

    @Bean
    CountDownLatch latch() {
      return new CountDownLatch(1);
    }

    @Bean
    ApplicationListener<HttpSessionCreatedEvent> listener(CountDownLatch latch, ArrayList<HttpSessionCreatedEvent> events){
      return event -> {
        events.add(event);
        latch.countDown();
      };
    }
  }



}
