package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import org.datavaultplatform.webapp.security.ScopedPermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@SpringBootTest
public class SecurityConfigTest {

  @Autowired
  PermissionEvaluator evaluator;

  @Autowired
  SecurityExpressionHandler expressionHandler;

  @Test
  void testEvaluatorWiring() throws NoSuchFieldException, IllegalAccessException {
    assertTrue(evaluator instanceof ScopedPermissionEvaluator);
    assertTrue(expressionHandler instanceof DefaultWebSecurityExpressionHandler);
    DefaultWebSecurityExpressionHandler def = (DefaultWebSecurityExpressionHandler) expressionHandler;
    Field f = AbstractSecurityExpressionHandler.class.getDeclaredField("permissionEvaluator");
    f.setAccessible(true);
    PermissionEvaluator actualEvaluator = (PermissionEvaluator)f.get(def);
    assertEquals(evaluator, actualEvaluator);
  }
}
