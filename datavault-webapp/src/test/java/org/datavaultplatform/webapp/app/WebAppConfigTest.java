package org.datavaultplatform.webapp.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

@ComponentScan("org.datavaultplatform.webapp.testcontrollers")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AddTestProperties
class WebAppConfigTest {

	@Autowired
	MockMvc mvc;

	@Test
	void testContextParam() throws Exception {
		mvc.perform(get("/test/context/param"))
				.andExpect(content().string("webapp.root"))
				.andDo(print());
	}

	@Test
	void testDisplayName() throws Exception {
		mvc.perform(get("/test/display/name"))
				.andExpect(content().string("datavault-webapp"))
				.andDo(print());
	}

	@Test
	void testSessionTimeout() throws Exception {
		mvc.perform(get("/test/session/timeout"))
				.andExpect(content().string("15"))
				.andDo(print());
	}

}
