package org.datavaultplatform.webapp.app.setup;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AddTestProperties
@Slf4j
public class ErrorPageTest {

  @Autowired
  TestRestTemplate restTemplate;

  @Test
  public void testErrorPageDirectly() {
    ResponseEntity<String> respEntity = restTemplate.getForEntity("/error", String.class);
    log.info(respEntity.toString());
    Assertions.assertThat(respEntity.getBody()).contains("An error has occured!");
    Assertions.assertThat(respEntity.getBody()).contains("Error code null returned for Unknown with message:");
  }

  @Test
  public void testErrorPageBecauseOfException() {
    ResponseEntity<String> respEntity = restTemplate.getForEntity("/test/oops", String.class);
    log.info(respEntity.toString());
    Assertions.assertThat(respEntity.getBody()).contains("An error has occured!");
    Assertions.assertThat(respEntity.getBody()).contains("SimulatedError");
  }

}
