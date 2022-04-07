package org.datavaultplatform.webapp.app.authentication.permission;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AddTestProperties
@AutoConfigureMockMvc
@Slf4j
public class PermissionEvaluatorTest {

  private static Info INFO_ONE = new Info(1, "info_one");
  private static Info INFO_TWO = new Info(2, "info_two");
  private static Info INFO_THREE = new Info(3, "info_three");

  private static List<Info> ALL_INFO = Arrays.asList(INFO_ONE, INFO_TWO, INFO_THREE);

  @Autowired
  MockMvc mvc;

  @Nested
  class Unsecure {

    @SneakyThrows
    String getInfoName(String id) {
      return mvc.perform(get("/test/info/unsecure/" + id))
          .andDo(print())
          .andExpect(status().is2xxSuccessful())
          .andReturn().getResponse().getContentAsString();
    }

    @ParameterizedTest
    @CsvSource({"1,info_one", "2,info_two", "3,info_three"})
    void testUnsecure(String id, String expectedInfoName) {
      assertThat(getInfoName(id)).isEqualTo(expectedInfoName);
    }
  }

  @Nested
  class Secure {

    @SneakyThrows
    MvcResult getInfoName(String id) {
      return mvc.perform(get("/test/info/secure/" + id))
          .andDo(print())
          .andReturn();
    }

    void checkNotAuthenticated() {
      MvcResult result = getInfoName("1");
      assertEquals("http://localhost/auth/login", result.getResponse().getRedirectedUrl());
      assertEquals(HttpStatus.FOUND.value(), result.getResponse().getStatus());
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
   * This TestConfig class adds the InfoController just for this test.
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

    @RequestMapping("/unsecure/{id}")
    public String getInfoUnsecure(@PathVariable("id") int id) {
      return getInfoInternal(id);
    }

    @RequestMapping("/secure/{id}")
    @PreAuthorize("isAuthenticated() and hasPermission(#id,'java.lang.String','READ')")
    public String getInfoSecure(@PathVariable("id") int id) {
      return getInfoInternal(id);
    }

    private String getInfoInternal(int id) {
      return ALL_INFO.stream()
          .filter(info -> id == info.getId())
          .map(Info::getName)
          .findFirst()
          .orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class Info {

    private int id;
    private String name;
  }

}
