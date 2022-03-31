package org.datavaultplatform.webapp.test;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
public abstract class TestUtils {

  private TestUtils() {
  }

  /*
  The setCookie header value looks like this : 'JSESSIONID=44310C5F21C6D853C8DC8EAEAEAC6D73; Path=/; HttpOnly'
 */
  public static String getSessionId(HttpHeaders headers){
    String setCookie = headers.get("Set-Cookie").get(0);
    log.info("Set-Cookie [{}]",setCookie);
    StringTokenizer parts = new StringTokenizer(setCookie, ";", false);
    String part1 = parts.nextToken();
    StringTokenizer parts2 = new StringTokenizer(part1, "=", false);
    parts2.nextToken(); //SKIP OVER 'JSESSIONID' token, we want the next token, the session Id
    String sessionId = parts2.nextToken();
    return sessionId;
  }

  public static SecurityContext getSecurityContext(MvcResult result){
    SecurityContext ctx = (SecurityContext) result.getRequest().getSession().getAttribute(SPRING_SECURITY_CONTEXT_KEY);
    return ctx;
  }

}