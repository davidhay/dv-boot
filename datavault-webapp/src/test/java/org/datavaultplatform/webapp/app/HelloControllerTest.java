package org.datavaultplatform.webapp.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.datavaultplatform.webapp.test.AddTestProperties;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AddTestProperties
public class HelloControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  void testHelloController() throws Exception {
    String id = "xyz123";
    mvc.perform(get("/test/hello?name=" + id))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(MockMvcResultMatchers.content().string(
            Matchers.containsString(id)));
  }
}
