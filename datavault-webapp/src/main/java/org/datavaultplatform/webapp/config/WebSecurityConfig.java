package org.datavaultplatform.webapp.config;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.auth.AuthenticationSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.session.SessionRegistry;

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

  //@Autowired
  //PermissionEvaluator evaluator;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.debug(securityDebug);
    //web.expressionHandler(getHandler("web"));
  }

  /*
  private DefaultWebSecurityExpressionHandler getHandler(final String context){
    DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler(){
      @Override
      public String toString(){
        return context + ":" + super.toString();
      }
    };
    handler.setPermissionEvaluator(evaluator);
    return handler;
  }
   */

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    authorizeRequests(http)
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

        .sessionManagement()
          .maximumSessions(1)
          .expiredUrl("/auth/login?security")
          .sessionRegistry(sessionRegistry);

    if (csrfDisabled) {
      // TODO - we only really want this for tests
      log.warn("CSRF PROTECTION DISABLED!!!!");
      http.csrf().disable();
    }
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

        //TODO : temporary rules
        .antMatchers("/test/**").permitAll() //TODO - this is temporary
        .antMatchers("/index").permitAll() //TODO - this is temporary
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

        //.expressionHandler(getHandler("http")) //TODO - do we need this ?
  }

}