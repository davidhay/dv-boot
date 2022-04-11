package org.datavaultplatform.webapp.config.standalone;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("standalone")
@ComponentScan({"org.datavaultplatform.webapp.controllers.standalone"})
@Import(StandaloneWebSecurityConfig.class)
public class StandaloneProfileConfig {

}
