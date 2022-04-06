package org.datavaultplatform.webapp.config;

import java.util.Properties;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${mail.administrator}")
  String to;

  //TODO - add property for mail.from
  @Value("${mail.from:feedback@datavaultplatform.org}")
  String from;

  //TODO - add property for mail.subject
  @Value("${mail.subject:DataVault feedback}")
  String subject;

  @Value("${mail.host}")
  private String mailHost;

  @Value("${mail.port}")
  private int mailPort;

  @Value("${mail.protocol:smtp}")
  String mailProtocol;


  @Bean
  SimpleMailMessage templateMessage() {
    SimpleMailMessage result = new SimpleMailMessage();
    result.setTo(to);
    result.setFrom(from);
    result.setSubject(subject);
    return result;
  }

  @Bean
  JavaMailSender mailSender() {
    JavaMailSenderImpl result = new JavaMailSenderImpl();
    result.setHost(mailHost);
    result.setPort(mailPort);
    result.setProtocol(mailProtocol);

    result.setJavaMailProperties(javaMailProperties());
    return result;
  }

  @Bean
  @ConfigurationProperties(prefix = "jmail")
  Properties javaMailProperties() {
    return new Properties();
  }

}
