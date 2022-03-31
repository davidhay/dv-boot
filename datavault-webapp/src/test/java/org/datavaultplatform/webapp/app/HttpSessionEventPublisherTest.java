package org.datavaultplatform.webapp.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.datavaultplatform.webapp.test.TestUtils.getSessionId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AddTestProperties
@Slf4j
/**
 * This class tests that the HttpSessionEventPublisher is working as expected.
 */
public class HttpSessionEventPublisherTest {

  @Autowired
  HttpSessionEventPublisher publisher;

  @Autowired
  TestRestTemplate template;

  @Autowired
  List<HttpSessionCreatedEvent> events;

  @Autowired
  CountDownLatch latch;

  @Test
  void testPublisherBeanExists() {
    assertNotNull(publisher);
  }

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

    /*
     * This bean registers itself as an HttpSessionCreatedEvent listener. It puts the events into a list.
     * The list can be seen by the test.
     * After is receives an event, it notifies the test via the latch.
     */
    @Bean
    ApplicationListener<HttpSessionCreatedEvent> listener(CountDownLatch latch, ArrayList<HttpSessionCreatedEvent> events){
      return event -> {
        events.add(event);
        latch.countDown();
      };
    }
  }



}
