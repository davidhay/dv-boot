package org.datavaultplatform.webapp.controllers;

import java.util.stream.Collectors;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/security/annotation/")
public class SecurityAnnotationController {

  @GetMapping(value = "/auth/roles")
  public String getAuthRoles(Authentication auth){
    return auth.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .sorted()
        .collect(Collectors.joining(":"));
  }

  @GetMapping(value = "/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String getForAdminOnly(){
    return "isAdmin";
  }


  @GetMapping(value = "/user")
  @PreAuthorize("hasRole('USER')")
  public String getForUserOnly(){
    return "isUser";
  }

  @GetMapping(value = "/admin/secured")
  @Secured("ROLE_ADMIN")
  public String getSecuredForAdminOnly(){
    return "isAdmin@Secured";
  }

}
