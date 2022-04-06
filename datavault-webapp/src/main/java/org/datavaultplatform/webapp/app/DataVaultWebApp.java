package org.datavaultplatform.webapp.app;

import static java.util.Collections.singletonList;

import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.config.ActutatorConfig;
import org.datavaultplatform.webapp.config.LdapConfig;
import org.datavaultplatform.webapp.config.MailConfig;
import org.datavaultplatform.webapp.config.MvcConfig;
import org.datavaultplatform.webapp.config.PrivilegeEvaluatorConfig;
import org.datavaultplatform.webapp.config.SecurityConfig;
import org.datavaultplatform.webapp.config.WebConfig;
import org.datavaultplatform.webapp.config.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@SpringBootApplication
@ComponentScan({
    "org.datavaultplatform.webapp.controllers.auth",
    "org.datavaultplatform.webapp.controllers.standalone",
    "org.datavaultplatform.webapp.services"})
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:datavault.properties")
})
@Import({WebConfig.class, MvcConfig.class, PrivilegeEvaluatorConfig.class, ActutatorConfig.class,
    SecurityConfig.class, WebSecurityConfig.class, MailConfig.class, LdapConfig.class})
@Slf4j
public class DataVaultWebApp implements CommandLineRunner {

  @Value("${spring.application.name}")
  String applicationName;

  @Autowired
  Environment env;

  public DataVaultWebApp(FreeMarkerConfigurer freeMarkerConfigurer) {
    freeMarkerConfigurer.getTaglibFactory()
        .setClasspathTlds(singletonList("/META-INF/security.tld"));
  }

  public static void main(String[] args) {
    SpringApplication.run(DataVaultWebApp.class, args);
  }

  @Override
  public void run(String... args) {
    log.info("java.version [{}]",env.getProperty("java.version"));
    log.info("java.vendor [{}]",env.getProperty("java.vendor"));
    log.info("os.arch [{}]",env.getProperty("os.arch"));
    log.info("os.name [{}]",env.getProperty("os.name"));
    log.info("active.profiles {}", (Object) env.getActiveProfiles());
  }
}
