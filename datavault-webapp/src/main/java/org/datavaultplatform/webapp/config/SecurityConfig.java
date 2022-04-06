package org.datavaultplatform.webapp.config;

import java.io.Serializable;
import org.datavaultplatform.webapp.auth.AuthenticationSuccess;
import org.datavaultplatform.webapp.services.NotifyLoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
public class SecurityConfig {

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  AuthenticationSuccess authenticationSuccess(NotifyLoginService service){
    return new AuthenticationSuccess(service);
  }

  /*
  @Bean
  public PermissionEvaluator permissionEvaluator(RestService restService){
    return new ScopedPermissionEvaluator(restService);
  }
   */
  /*
   TODO : temporary
   */
  @Bean
  public PermissionEvaluator permissionEvaluator(){
    return new PermissionEvaluator() {
      @Override
      public boolean hasPermission(Authentication authentication, Object targetDomainObject,
          Object permission) {
        return true;
      }

      @Override
      public boolean hasPermission(Authentication authentication, Serializable targetId,
          String targetType, Object permission) {
        return true;
      }
    };
  }


}
