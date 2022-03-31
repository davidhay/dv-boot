package org.datavaultplatform.webapp.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AddTestProperties
public class SpringContextTest {

  @Autowired
  WebApplicationContext wac;

  @Test
  void test() {
    assertNotNull(wac);
  }
}
