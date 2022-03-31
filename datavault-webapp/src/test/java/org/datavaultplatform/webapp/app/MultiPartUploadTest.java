package org.datavaultplatform.webapp.app;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AddTestProperties
public class MultiPartUploadTest {

  @Value("classpath:images/logo-dvsmall.jpg")
  Resource dvLogo;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void testUploadFile() throws Exception {

    String filename = dvLogo.getFilename();
    System.out.printf("FILENAME IS [%s]%n", filename );
    MockMultipartFile file = new MockMultipartFile("file",filename, MediaType.IMAGE_JPEG_VALUE, dvLogo.getInputStream());

    long expectedSize = dvLogo.contentLength();
    String expectedResult = String.format("name[file]type[image/jpeg]size[%d]", expectedSize);

    mockMvc.perform(multipart("/test/upload/file").file(file).with(csrf()))
        .andDo(print())
        .andExpect(content().string(expectedResult))
        .andExpect(status().isOk());

  }

  @Test
  public void testUploadMulti() throws Exception {

    String filename = dvLogo.getFilename();
    System.out.printf("FILENAME IS [%s]%n", filename );
    MockMultipartFile file = new MockMultipartFile("file",filename, MediaType.IMAGE_JPEG_VALUE, dvLogo.getInputStream());

    String person = "{\"first\":\"James\",\"last\":\"Bond\"}";

    MockMultipartFile personFile = new MockMultipartFile("person", null,
        "application/json", person.getBytes());

    long expectedSize = dvLogo.contentLength();
    String expectedResult = String.format("name[file]type[image/jpeg]size[%d]first[James]last[Bond]", expectedSize);

    mockMvc.perform(multipart("/test/upload/multi")
            .file(file)
            .file(personFile)
            .with(csrf()))
        .andDo(print())
        .andExpect(content().string(expectedResult))
        .andExpect(status().isOk());

  }

}
