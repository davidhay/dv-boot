package org.datavaultplatform.webapp.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.config.logging.LoggingInterceptor;
import org.datavaultplatform.webapp.services.ApiErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!standalone")
@Slf4j
public class RestTemplateConfig {

  /*
   * If we don't preconfigure the timeouts on restTemplate it can retry connections for up to 5 minutes!
   */
  @Bean
  RestTemplate restTemplate(@Value("${broker.timeout.ms:1000}") int brokerTimeoutMs) {
    RestTemplate result = new RestTemplate(getRequestFactory(brokerTimeoutMs));
    result.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
    result.setErrorHandler(new ApiErrorHandler());
    return  result;
  }

  private ClientHttpRequestFactory getRequestFactory(int brokerTimeoutMs) {
    HttpComponentsClientHttpRequestFactory inner = new HttpComponentsClientHttpRequestFactory();
    inner.setConnectTimeout(brokerTimeoutMs);
    inner.setReadTimeout(brokerTimeoutMs);
    return new BufferingClientHttpRequestFactory(inner);
  }

}
