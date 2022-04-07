package org.datavaultplatform.webapp.app.authentication.permission;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AddTestProperties
@AutoConfigureMockMvc
@Slf4j
class PermissionEvaluatorTest {

  @Autowired
  MockMvc mvc;

  @Nested
  class Unsecure {

    @ParameterizedTest
    @CsvSource({"1,info_one", "2,info_two", "3,info_three"})
    @SneakyThrows
    void testUnsecure(String id, String expectedInfoName) {
      mvc.perform(get("/test/info/unsecure/" + id))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().string(expectedInfoName));
    }
  }

  @Nested
  class Secure {

    @SneakyThrows
    MvcResult getInfoName(String id) {
      return mvc.perform(get("/test/info/secure/" + id))
          .andReturn();
    }

    void checkNotAuthenticated() {
      MvcResult result = getInfoName("1");
      assertEquals(HttpStatus.FOUND.value(), result.getResponse().getStatus());
      assertEquals("http://localhost/auth/login", result.getResponse().getRedirectedUrl());
    }

    @SneakyThrows
    void checkAllowed() {
      MvcResult result = getInfoName("1");
      assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
      assertEquals("info_one", result.getResponse().getContentAsString());
    }

    @Test
    void testNotAuthenticatedOne() {
      checkNotAuthenticated();
    }

    @Test
    @WithAnonymousUser
    void testNotAuthenticatedTwo() {
      checkNotAuthenticated();
    }

  }

  /**
   * This TestConfig class adds the InfoController for this test.
   */
  @TestConfiguration
  static class TestConfig {

    @Bean
    InfoController infoController() {
      return new InfoController();
    }

  }

  @RestController
  @RequestMapping("/test/info")
  static class InfoController {

    Map<Integer,String> info = new HashMap<Integer,String>(){{
      put(1, "info_one");
      put(2, "info_two");
      put(3, "info_three");
    }};

    @GetMapping("/unsecure/{id}")
    String getInfoUnsecure(@PathVariable int id) {
      return info.get(id);
    }

    @PreAuthorize("isAuthenticated() and hasPermission(#id,'java.lang.String','READ')")
    @GetMapping("/secure/{id}")
    String getInfoSecure(@PathVariable int id) {
      return info.get(id);
    }
  }
}
