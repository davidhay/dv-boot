package org.datavaultplatform.webapp.app.authentication;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.datavaultplatform.common.model.Group;
import org.datavaultplatform.common.request.ValidateUser;
import org.datavaultplatform.webapp.services.RestService;
import org.datavaultplatform.webapp.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("database")
@AutoConfigureMockMvc
@TestPropertySource(properties = "ldap.password=dummy-password")
public class LoginUsingDatabaseTest {

  @LocalServerPort
  int localServerPort;

  @Autowired
  TestRestTemplate template;

  @MockBean
  RestService mRestService;

  @Captor
  ArgumentCaptor<ValidateUser> argValidateUser;

  @Test
  void testLoginUsesRestService() {

    Mockito.when(mRestService.isValid(argValidateUser.capture())).thenReturn(true);
    Mockito.when(mRestService.getGroups()).thenReturn(new Group[0]);

    ResponseEntity<String> responseEntity = TestUtils.login(template, "dbuser", "dbpassword");

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(responseEntity.getHeaders().getFirst("Location")).isEqualTo(String.format("http://localhost:%d/",localServerPort));
  }
}
