package org.datavaultplatform.webapp.config.shib;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.auth.shib.ShibAuthenticationFilter;
import org.datavaultplatform.webapp.auth.shib.ShibAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ShibWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.debug:false}")
  boolean securityDebug;

  @Autowired
  SessionRegistry sessionRegistry;

  @Autowired
  ShibAuthenticationProvider shibAuthenticationProvider;

  @Autowired
  Http403ForbiddenEntryPoint http403EntryPoint;

  @Autowired
  ShibAuthenticationFilter filter;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.debug(securityDebug);

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.addFilterAt(filter, AbstractPreAuthenticatedProcessingFilter.class);

    authorizeRequests(http)
        .and()

        .sessionManagement()
          .maximumSessions(1)
          .expiredUrl("/auth/login?security")
          .sessionRegistry(sessionRegistry)
          .and();

    http.exceptionHandling(ex -> ex.authenticationEntryPoint(http403EntryPoint));

  }

  ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests(
      HttpSecurity http) throws Exception {
    return http.authorizeRequests()
        .antMatchers("/resources/**").permitAll() //OKAY
        .antMatchers("/actuator/info").permitAll()  //OKAY
        .antMatchers("/actuator/status").permitAll()  //OKAY
        .antMatchers("/actuator/customtime").permitAll()  //OKAY
        .antMatchers("/error").permitAll()      //OKAY
        .antMatchers("/auth/**").permitAll()      //OKAY

        .antMatchers("/actuator/**").permitAll()  //TODO - needs better security

        .antMatchers("/admin").access("hasRole('ROLE_ADMIN')")
        .antMatchers("/admin/").access("hasRole('ROLE_ADMIN')")
        .antMatchers("/admin/archivestores/**").access("hasRole('ROLE_ADMIN_ARCHIVESTORES')")
        .antMatchers("/admin/billing/**").access("hasRole('ROLE_ADMIN_BILLING')")
        .antMatchers("/admin/deposits/**").access("hasRole('ROLE_ADMIN_DEPOSITS')")
        .antMatchers("/admin/events/**").access("hasRole('ROLE_ADMIN_EVENTS')")
        .antMatchers("/admin/retentionpolicies/**").access("hasRole('ROLE_ADMIN_RETENTIONPOLICIES')")
        .antMatchers("/admin/retrieves/**").access("hasRole('ROLE_ADMIN_RETRIEVES')")
        .antMatchers("/admin/roles/**").access("hasRole('ROLE_ADMIN_ROLES')")
        .antMatchers("/admin/schools/**").access("hasRole('ROLE_ADMIN_SCHOOLS')")
        .antMatchers("/admin/vaults/**").access("hasRole('ROLE_ADMIN_VAULTS')")
        .antMatchers("/admin/reviews/**").access("hasRole('ROLE_ADMIN_REVIEWS')")

        //TODO : double check whether this has to come last
        .antMatchers("/**").access("hasRole('ROLE_USER')"); //OKAY
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(shibAuthenticationProvider);
  }

}