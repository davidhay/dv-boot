package org.datavaultplatform.webapp.config;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.controllers.authentication.AuthenticationSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.debug:false}")
  boolean securityDebug;

  @Value("${datavault.csrf.disabled:false}")
  boolean csrfDisabled;

  @Autowired
  SessionRegistry sessionRegistry;

  @Autowired
  AuthenticationSuccess authenticationSuccess;

  @Autowired
  PermissionEvaluator evaluator;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.debug(securityDebug);

    DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
    handler.setPermissionEvaluator(evaluator);
    web.expressionHandler(handler);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .authorizeRequests()
          .antMatchers("/resources/**").permitAll() //OKAY
          .antMatchers("/actuator/**").permitAll()  //OKAY
          .antMatchers("/error**").permitAll()      //OKAY
          .antMatchers("/auth/**").permitAll()      //OKAY

          //TODO : temporary rules
          .antMatchers("/test/**").permitAll() //TODO - this is temporary
          .antMatchers("/index*").permitAll() //TODO - this is temporary

          .antMatchers("/**").access("hasRole('ROLE_USER')") //OKAY
          .and()
        .formLogin()
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/security_check")
            .failureUrl("/auth/login?error=true")
            .defaultSuccessUrl("/")
            .successHandler(authenticationSuccess)
            .and()
        .logout()
          .logoutUrl("/auth/logout")
          .logoutSuccessUrl("/auth/login?logout")
          .and()
        .exceptionHandling()
          .accessDeniedPage("/auth/denied")
          .and()
        .sessionManagement().maximumSessions(1).expiredUrl("/auth/login?security").sessionRegistry(sessionRegistry);

      if(csrfDisabled) {
        log.warn("CSRF PROTECTION DISABLED!!!!");
        http.csrf().disable();
      }
  }

}