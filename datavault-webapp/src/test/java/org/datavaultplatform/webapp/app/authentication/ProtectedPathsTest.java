package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("standalone")
@AutoConfigureMockMvc
public class ProtectedPathsTest {

  private static final String ROLE_XXX = "XXX";
  private static final AtomicInteger COUNTER = new AtomicInteger();
  private static final int EXPECTED_TESTS = 13;

  @Autowired
  MockMvc mvc;

  @ParameterizedTest(name = "[{index}] path={0}, role={1}")
  @CsvFileSource(resources = "/protected-paths.csv", numLinesToSkip = 1)
  void testPathRequiresRole(String path, String roleWithPrefix) {
    String role = roleWithPrefix.replaceFirst("^ROLE_", "");

    checkPathCanBeAccessedWithRole(path, role);

    checkPathCannotBeAccessedWithoutRole(path);

    COUNTER.incrementAndGet();
  }

  @SneakyThrows
  MvcResult accessPathUsingRole(String path, String role) {
    return mvc.perform(get(path)
            .with(user("testuser")
                  .password("pass")
                  .roles(role)))
            .andReturn();
  }

  void checkPathCanBeAccessedWithRole(String path, String role) {
    MvcResult result = accessPathUsingRole(path, role);

    // We are testing the paths without any mappings from paths to controllers.
    // We should get 404 NOT FOUND, if we pass the security checks
    assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
  }

  void checkPathCannotBeAccessedWithoutRole(String path) {
    MvcResult result = accessPathUsingRole(path, ROLE_XXX);
    // We are testing the paths without any mappings from paths to controllers.
    // We should get 403 FORBIDDEN, when we fail the security checks
    assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
  }

  @BeforeAll
  static void setup() {
    COUNTER.set(0);
  }

  @AfterAll
  static void tearDown() {
    assertEquals(EXPECTED_TESTS, COUNTER.intValue());
  }

}
