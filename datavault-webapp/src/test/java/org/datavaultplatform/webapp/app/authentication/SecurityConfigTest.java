package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@SpringBootTest
public class SecurityConfigTest {

  @Autowired
  SecurityExpressionHandler expressionHandler;

  @Test
  void testEvaluatorWiring(ApplicationContext ctx) {

    assertThrows(NoSuchBeanDefinitionException.class, () -> {
      ctx.getBean(PermissionEvaluator.class);
    });

    assertTrue(expressionHandler instanceof DefaultWebSecurityExpressionHandler);

    //PermissionEvaluator evaluator = ctx.getBean(PermissionEvaluator.class);
    /*
    assertTrue(evaluator instanceof ScopedPermissionEvaluator);
    assertTrue(expressionHandler instanceof DefaultWebSecurityExpressionHandler);
    DefaultWebSecurityExpressionHandler def = (DefaultWebSecurityExpressionHandler) expressionHandler;
    Field f = AbstractSecurityExpressionHandler.class.getDeclaredField("permissionEvaluator");
    f.setAccessible(true);
    PermissionEvaluator actualEvaluator = (PermissionEvaluator)f.get(def);
    assertEquals(evaluator, actualEvaluator);
     */
  }
}
