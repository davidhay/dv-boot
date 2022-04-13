package org.datavaultplatform.webapp.config.standalone;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.authentication.AuthenticationSuccess;
import org.datavaultplatform.webapp.config.HttpSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;

@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class StandaloneWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.debug:false}")
  boolean securityDebug;

  @Value("${datavault.csrf.disabled:false}")
  boolean csrfDisabled;

  @Autowired
  SessionRegistry sessionRegistry;

  @Autowired
  AuthenticationSuccess authenticationSuccess;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.debug(securityDebug);
    //TODO - do we need to do this?
    //web.expressionHandler(expressionHandler);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    HttpSecurityUtils.sessionManagement(http, sessionRegistry);

    HttpSecurityUtils.authorizeRequests(http, true);

    HttpSecurityUtils.formLogin(http, authenticationSuccess);

    if (csrfDisabled) {
      //only for testing 'standalone' profile
      log.warn("CSRF PROTECTION DISABLED!!!!");
      http.csrf().disable();
    }
  }
}