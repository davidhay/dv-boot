package org.datavaultplatform.webapp.app;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.utility.XmlEscape;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@WebMvcTest
@AddTestProperties
public class FreemarkerConfigTest {

  Field fPrefix = UrlBasedViewResolver.class.getDeclaredField("prefix");
  Field fSuffix = UrlBasedViewResolver.class.getDeclaredField("suffix");
  Field fContentType = UrlBasedViewResolver.class.getDeclaredField("contentType");
  Field fPreferFileSystemAccess = FreeMarkerConfigurationFactory.class.getDeclaredField("preferFileSystemAccess");

  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  @Autowired
  private FreeMarkerViewResolver freeMarkerViewResolver;

  {
    fPrefix.setAccessible(true);
    fSuffix.setAccessible(true);
    fContentType.setAccessible(true);
    fPreferFileSystemAccess.setAccessible(true);
  }

  public FreemarkerConfigTest() throws NoSuchFieldException {
  }

  @Test
  void testFreemarkerConfigurer() throws IOException, IllegalAccessException {
    assertNotNull(freeMarkerConfigurer);
    Configuration config = freeMarkerConfigurer.getConfiguration();

    Boolean preferFS = (Boolean)fPreferFileSystemAccess.get(freeMarkerConfigurer);
    assertTrue(preferFS);

    TemplateLoader loader = config.getTemplateLoader();
    Object helloTemplateSource = loader.findTemplateSource("hello.ftl");
    String helloTemplateSourceStr = helloTemplateSource.toString();
    assertThat(helloTemplateSourceStr).endsWith("classes/freemarker/hello.ftl");

    Object errorTemplatesource = loader.findTemplateSource("error/error.ftl");
    String errorTemplatesourceStr = errorTemplatesource.toString();
    assertThat(errorTemplatesourceStr).endsWith("classes/freemarker/error/error.ftl");

    XmlEscape esc = (XmlEscape) config.getSharedVariable("xml_escape");
    Assertions.assertNotNull(esc);

    assertEquals("UTF-8", config.getURLEscapingCharset());

    ClassPathResource res1 = new ClassPathResource("freemarker/hello.ftl");
    assertEquals("<!DOCTYPE html><!--hello.ftl-->", getFirstLine(res1));

    ClassPathResource res2 = new ClassPathResource("freemarker/nested/nested.ftl");
    assertEquals("<!DOCTYPE html><!--nested.ftl-->", getFirstLine(res2));
  }

  public String getFirstLine(ClassPathResource res) throws IOException {
    InputStreamReader rdr = new InputStreamReader(res.getInputStream());
    LineNumberReader lnr = new LineNumberReader(rdr);
    return lnr.readLine();
  }

  @Test
  void testViewResolver() throws IllegalAccessException {

    //VIEW RESOLVER CONFIG
    String prefix = (String) fPrefix.get(freeMarkerViewResolver);
    assertEquals("", prefix);

    String suffix = (String) fSuffix.get(freeMarkerViewResolver);
    assertEquals(".ftl", suffix);

    String contentType = (String) fContentType.get(freeMarkerViewResolver);
    assertEquals("text/html;charset=UTF-8", contentType);

  }

}
