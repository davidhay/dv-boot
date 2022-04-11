package org.datavaultplatform.webapp.config.database;

import org.datavaultplatform.webapp.auth.database.DatabaseAuthenticationProvider;
import org.datavaultplatform.webapp.services.PermissionsService;
import org.datavaultplatform.webapp.services.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("database")
@Import(DatabaseWebSecurityConfig.class)
public class DatabaseProfileConfig {

  /*
   * Spring Boot should see this AuthenticationProvider and create an AuthenticationManager to use it
   */
  @Bean
  DatabaseAuthenticationProvider databaseAuthenticationProvider(RestService restService, PermissionsService permissionsService){
    return new DatabaseAuthenticationProvider(restService, permissionsService);
  }

}
