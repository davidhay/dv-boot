package org.datavaultplatform.webapp.app.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import org.datavaultplatform.webapp.test.AddTestProperties;
import org.datavaultplatform.webapp.test.TestClockConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AddTestProperties
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
public class ActuatorTest {

  @Autowired
  ObjectMapper mapper;

  @Autowired
  MockMvc mvc;

  @Value("#{'${management.endpoints.web.exposure.include}'.split(',')}")
  private Set<String> endpoints;

  @Test
  void testInfo() throws Exception {
    mvc.perform(get("/actuator/info"))
        .andExpect(jsonPath("$.app.name").value(Matchers.is("datavault-webapp")))
        .andExpect(jsonPath("$.app.description").value(Matchers.is("webapp for datavault")))
        .andExpect(jsonPath("$.git.commit.time").exists())
        .andExpect(jsonPath("$.git.commit.id").value(Matchers.is("a16f01e")))
        .andExpect(jsonPath("$.build.artifact").value(Matchers.is("datavault-webapp")))
        .andExpect(jsonPath("$.build.time").value(Matchers.is("2022-03-30T12:41:50.383Z")))
        .andExpect(jsonPath("$.java.vendor").exists())
        .andExpect(jsonPath("$.java.runtime.version").exists())
        .andExpect(jsonPath("$.java.jvm.version").exists());
  }

  @Test
  void testCurrentTime() throws Exception {
    MvcResult mvcResult = mvc.perform(
            get("/actuator/customtime"))
        .andExpect(content().contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json"))
        .andExpect(jsonPath("$.current-time").exists())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    Map<String,String> infoMap = mapper.createParser(json).readValueAs(Map.class);

    Assertions.assertTrue(infoMap.containsKey("current-time"));
    String ct = infoMap.get("current-time");
    Assertions.assertEquals("Tue Mar 29 14:15:16 BST 2022",ct);
  }

  @Test
  void testEndpoints() throws Exception {

    assertEquals(ImmutableSet.of("env","beans","info","health","customtime"), endpoints);

    ResultActions temp = mvc.perform(get("/actuator"))
        .andExpect(status().isOk());

    for(String endpoint : endpoints){
      temp.andExpect(jsonPath("$._links."+endpoint).exists());
    }

  }
}
