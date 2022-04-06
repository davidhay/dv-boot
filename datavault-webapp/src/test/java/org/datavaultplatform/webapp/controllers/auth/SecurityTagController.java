package org.datavaultplatform.webapp.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test/security/tag/")
public class SecurityTagController {

  @GetMapping(value = "/basic")
  public String getTestPageForBasicSecurityTags() {
    return "test/securityTagBasic";
  }

}
