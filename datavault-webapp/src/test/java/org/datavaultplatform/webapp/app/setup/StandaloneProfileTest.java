package org.datavaultplatform.webapp.app.setup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("standalone")
@SpringBootTest
public class StandaloneProfileTest {

  @Autowired
  CountDownLatch latch;

  @Test
  void testContextIsCorrect() throws InterruptedException {
      assertTrue(latch.await(3, TimeUnit.SECONDS));
  }

  @TestConfiguration
  static class TestConfig implements ApplicationListener<ApplicationStartedEvent>{

    @Bean
    CountDownLatch latch(){
      return new CountDownLatch(1);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
      latch().countDown();
    }
  }

}
