package org.datavaultplatform.webapp.app.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import lombok.SneakyThrows;
import org.datavaultplatform.webapp.test.DebugSpringSecurity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class SecurityTagTest {

  private static String PREFIX = "/test/security/tag";

  @Autowired
  MockMvc mvc;

  /**
   * This method just checks that the correct ftl page is returned.
   */
  @Test
  void testPageWithNoUser()  {
    Document doc = getTestPage();
    Element element = doc.selectFirst("#for_all");
    assertEquals("DIV FOR ALL", element.text());
  }

  @Test
  @WithMockUser(roles="USER")
  void testPageWithUserRole()  {
    Document doc = getTestPage();
    checkForUsersSection(doc, true);
    checkForAdminsSection(doc, false);
  }

  @Test
  @WithMockUser(roles="ADMIN")
  void testPageWithAdminRole()  {
    Document doc = getTestPage();
    checkForAdminsSection(doc, true);
    checkForUsersSection(doc, false);
  }

  @SneakyThrows
  Document getTestPage() {
    MvcResult result = mvc.perform(
            get(PREFIX + "/basic"))
        .andReturn();
    assertEquals(HttpStatus.OK.value(),result.getResponse().getStatus());
    Document doc = Jsoup.parse(result.getResponse().getContentAsString());
    return doc;
  }

  void checkForUsersSection(Document doc, boolean shouldExist){
    Element element = doc.selectFirst("#users");
    if(shouldExist){
      assertEquals("DIV FOR USERS", element.text());
    }else{
      assertNull(element);
    }
  }

  void checkForAdminsSection(Document doc, boolean shouldExist){
    Element element = doc.selectFirst("#admins");
    if(shouldExist){
      assertEquals("DIV FOR ADMINS", element.text());
    }else{
      assertNull(element);
    }
  }

}
