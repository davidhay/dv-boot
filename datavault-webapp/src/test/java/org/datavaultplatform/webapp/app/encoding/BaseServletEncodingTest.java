package org.datavaultplatform.webapp.app.encoding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.datavaultplatform.webapp.app.AddTestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AddTestProperties
abstract class BaseServletEncodingTest {


  @Autowired
  MockMvc mvc;

  public abstract String getEncoding();

  @Test
  void testEncoding() throws Exception {

    mvc.perform(get("/test/time"))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset="+getEncoding()));
  }
}
