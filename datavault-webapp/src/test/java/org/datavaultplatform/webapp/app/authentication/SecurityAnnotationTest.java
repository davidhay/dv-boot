package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import lombok.SneakyThrows;
import org.datavaultplatform.webapp.test.DebugSpringSecurity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAnnotationTest {

  private static String PREFIX = "/test/security/annotation";

  @Autowired
  MockMvc mvc;

  /**
   * This method just checks that the mock user works and controller sees the roles we specified.
   *
   * @throws Exception
   */
  @Test
  @WithMockUser(roles = {"USER", "ADMIN"})
  @SneakyThrows
  void testUserRoles()  {
    MvcResult result = mvc.perform(
            get(PREFIX + "/auth/roles"))
        .andReturn();
    assertEquals("ROLE_ADMIN:ROLE_USER", result.getResponse().getContentAsString());
  }

  void checkDenied(MvcResult result) {
    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
    assertEquals("/auth/denied", result.getResponse().getForwardedUrl());
  }

  void checkOkay(MvcResult result) {
    assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
  }


  /**
   * These tests check that the @PreAuthorize("hasRole('ADMIN')") annotations are working
   */
  @Nested
  class AdminRoleTests {

    @SneakyThrows
    MvcResult checkAdminOnlyPage() {
      return mvc.perform(
              get(PREFIX + "/admin"))
          .andReturn();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testNoAdminIsDenied() {
      MvcResult result = checkAdminOnlyPage();
      checkDenied(result);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminIsAllowed() {
      MvcResult result = checkAdminOnlyPage();
      checkOkay(result);
    }
  }

  /**
   * These tests check that the @PreAuthorize("hasRole('USER')") annotations are working
   */
  @Nested
  class UserRoleTests {

    @SneakyThrows
    MvcResult checkUserOnlyPage() {
      return mvc.perform(
              get(PREFIX + "/user"))
          .andReturn();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNoUserIsDenied() {
      MvcResult result = checkUserOnlyPage();
      checkDenied(result);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserIsAllowed() {
      MvcResult result = checkUserOnlyPage();
      checkOkay(result);
    }
  }

  /**
   * These tests check that the @Secured("ADMIN_ROLE") annotations are working
   */
  @Nested
  class SecuredAdminRoleTests {

    @SneakyThrows
    MvcResult checkAdminOnlyPage() {
      return mvc.perform(
              get(PREFIX + "/admin/secured"))
          .andReturn();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testNoAdminIsDenied() {
      MvcResult result = checkAdminOnlyPage();
      checkDenied(result);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminIsAllowed() {
      MvcResult result = checkAdminOnlyPage();
      checkOkay(result);
    }
  }


}
