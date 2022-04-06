package org.datavaultplatform.webapp.config;

import org.datavaultplatform.webapp.auth.AuthenticationSuccess;
import org.datavaultplatform.webapp.services.NotifyLoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

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

}
