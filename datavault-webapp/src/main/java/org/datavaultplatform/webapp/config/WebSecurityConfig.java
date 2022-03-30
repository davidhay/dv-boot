package org.datavaultplatform.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.ConcurrencyControlConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  SessionRegistry sessionRegistry;

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
            .and()
        .logout()
          .logoutUrl("/auth/logout")
          .logoutSuccessUrl("/auth/login?logout")
          .and()
        .exceptionHandling()
          .accessDeniedPage("/auth/denied")
          .and()
        .sessionManagement()
          .sessionConcurrency(con ->
              con.expiredUrl("/auth/login?security") //TODO : not sure how to test this
                  .sessionRegistry(sessionRegistry));
  }
}