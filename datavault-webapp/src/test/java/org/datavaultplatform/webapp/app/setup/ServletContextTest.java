package org.datavaultplatform.webapp.app.setup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.datavaultplatform.webapp.test.AddTestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

@ComponentScan("org.datavaultplatform.webapp.test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AddTestProperties
class ServletContextTest {

	@Autowired
	MockMvc mvc;

	@Test
	void testContextParam() throws Exception {
		mvc.perform(get("/test/context/param"))
				.andExpect(content().string("webapp.root"));
	}

	@Test
	void testDisplayName() throws Exception {
		mvc.perform(get("/test/display/name"))
				.andExpect(content().string("datavault-webapp"));
	}

	@Test
	void testSessionTimeout() throws Exception {
		mvc.perform(get("/test/session/timeout"))
				.andExpect(content().string("15"));
	}

}
