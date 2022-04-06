package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import lombok.SneakyThrows;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@SpringBootTest
@AddTestProperties
public class SecurityConfigTest {

  @Autowired
  @Qualifier("webSecurityExpressionHandler")
  DefaultWebSecurityExpressionHandler expressionHandler;

  @Autowired
  @Qualifier("permissionEvaluator")
  PermissionEvaluator permissionEvaluator;

  @Test
  @SneakyThrows
  void testEvaluatorWiring() {
    Field f = AbstractSecurityExpressionHandler.class.getDeclaredField("permissionEvaluator");
    f.setAccessible(true);
    PermissionEvaluator actualEvaluator = (PermissionEvaluator)f.get(expressionHandler);
    assertEquals(permissionEvaluator, actualEvaluator);
  }
}
