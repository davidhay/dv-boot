package org.datavaultplatform.webapp.app.setup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = "mail.administrator=mail.admin@test.com")
public class MailSetupTest {

  @Autowired
  MailSender sender;

  @Autowired
  SimpleMailMessage simpleMailMessage;

  @Test
  void testMailSenderConfig(){
      assertThat(sender).isInstanceOf(JavaMailSenderImpl.class);
      JavaMailSenderImpl impl = (JavaMailSenderImpl) sender;

      assertThat(impl.getProtocol()).isEqualTo("smtp");
      assertThat(impl.getHost()).isEqualTo("smtp.gmail.com");
      assertThat(impl.getPort()).isEqualTo(587);

      Properties props = impl.getJavaMailProperties();
      assertThat(props.getProperty("mail.smtp.auth")).isEqualTo("false");
      assertThat(props.getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
      assertThat(props.getProperty("mail.smtp.quitwait")).isEqualTo("true");

      assertThat(props.size()).isEqualTo(3);
  }

  @Test
  void testMailMessageConfigConfig() {
    assertThat(simpleMailMessage.getSubject()).isEqualTo("DataVault feedback");
    assertThat(simpleMailMessage.getFrom()).isEqualTo("feedback@datavaultplatform.org");
    assertThat(simpleMailMessage.getTo().length).isOne();
    assertThat(simpleMailMessage.getTo()[0]).isEqualTo("mail.admin@test.com");
  }

}
