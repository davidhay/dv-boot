package org.datavaultplatform.webapp.app;

import static java.util.Collections.singletonList;

import org.datavaultplatform.webapp.config.ActutatorConfig;
import org.datavaultplatform.webapp.config.MvcConfig;
import org.datavaultplatform.webapp.config.PrivilegeEvaluatorConfig;
import org.datavaultplatform.webapp.config.SecurityConfig;
import org.datavaultplatform.webapp.config.WebConfig;
import org.datavaultplatform.webapp.config.WebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@SpringBootApplication
@ComponentScan({"org.datavaultplatform.webapp.controllers","org.datavaultplatform.webapp.services"})
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:datavault.properties")
})
@Import({WebConfig.class, MvcConfig.class, PrivilegeEvaluatorConfig.class, ActutatorConfig.class,
    SecurityConfig.class, WebSecurityConfig.class})
public class WebApplication {

  public WebApplication(FreeMarkerConfigurer freeMarkerConfigurer) {
    freeMarkerConfigurer.getTaglibFactory().setClasspathTlds(singletonList("/META-INF/security.tld"));
  }

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

}