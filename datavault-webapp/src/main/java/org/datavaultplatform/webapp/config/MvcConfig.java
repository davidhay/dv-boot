package org.datavaultplatform.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/resources/**")
        .addResourceLocations("classpath:/static/");
  }

  @Override
  public void addViewControllers (ViewControllerRegistry registry) {

    {
      //TODO : temporary
      //mapping url to a view
      ViewControllerRegistration r = registry.addViewController("/index");
      r.setViewName("index");
      //setting status code
      r.setStatusCode(HttpStatus.OK);
    }

    {
      //TODO : temporary
      //mapping url to a view
      ViewControllerRegistration r = registry.addViewController("/secure");
      r.setViewName("secure");
      //setting status code
      r.setStatusCode(HttpStatus.OK);
    }
  }

}
