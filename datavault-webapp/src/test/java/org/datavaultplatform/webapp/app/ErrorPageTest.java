package org.datavaultplatform.webapp.app;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ErrorPageTest {

  @Autowired
  TestRestTemplate restTemplate;

  @Test
  public void testErrorPageDirectly() {
    ResponseEntity<String> respEntity = restTemplate.getForEntity("/error", String.class);
    System.out.println(respEntity);
    Assertions.assertThat(respEntity.getBody()).contains("An error has occured!");
    Assertions.assertThat(respEntity.getBody()).contains("Error code null returned for Unknown with message:");
  }

  @Test
  public void testErrorPageBecauseOfException() {
    ResponseEntity<String> respEntity = restTemplate.getForEntity("/oops", String.class);
    System.out.println(respEntity);
    Assertions.assertThat(respEntity.getBody()).contains("An error has occured!");
    Assertions.assertThat(respEntity.getBody()).contains("SimulatedError");
  }

}
