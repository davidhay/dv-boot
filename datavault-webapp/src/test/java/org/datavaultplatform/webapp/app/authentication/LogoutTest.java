package org.datavaultplatform.webapp.app.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.datavaultplatform.webapp.test.TestUtils.getSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@AddTestProperties
@WithMockUser(username = "mUser")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LogoutTest {

  @Autowired
  MockMvc mvc;

  MockHttpSession getMockSession() {
    MockHttpSession result = new MockHttpSession();
    assertThat(result.isInvalid()).isFalse();
    return result;
  }

  /**
   * Just checks that the @WithMockUser is working as expected
   * Does not test logout
   */
  @Test
  void testMockUser() throws Exception {
    MockHttpSession mSession = getMockSession();

    MvcResult result =
        mvc.perform(get("/secure").session(mSession))
            .andExpect(authenticated())
            .andDo(print())
            .andReturn();
    SecurityContext ctx = getSecurityContext(result);
    Authentication auth = ctx.getAuthentication();
    assertThat(auth.getName()).isEqualTo("mUser");

    //after accessing the secure page, the session is still VALID
    assertThat(!mSession.isInvalid());
  }

  /*
   * GIVEN an authenticated user
   * WHEN they logout
   * THEN their session is invalidated
   * AND there is no SecurityContext in the session
   * AND they are redirected to /auth/confirmation
   */
  @Test
  void testMockUserLogout() throws Exception {
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("mUser");
    performLogoutCheck();
  }

  /**
   * Tests that logout can still be accessed by someone who is not logged in
   * @throws Exception
   */
  @Test
  @WithAnonymousUser
  void testLogoutWhenNotLoggedIn() throws Exception {
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(
        AnonymousAuthenticationToken.class);
    performLogoutCheck();
  }

  void performLogoutCheck() throws Exception {
    MockHttpSession mSession = getMockSession();

    MvcResult result =
        mvc.perform(get("/auth/logout").session(mSession))
            .andDo(print())
            .andReturn();

    //AFTER LOGGING OUT - the session is invalid
    assertThat(mSession.isInvalid());

    //Check there is no SecurityContext in session
    SecurityContext ctx = getSecurityContext(result);
    Assertions.assertNull(ctx);

    assertEquals("/auth/confirmation", result.getResponse().getRedirectedUrl());
  }


}
