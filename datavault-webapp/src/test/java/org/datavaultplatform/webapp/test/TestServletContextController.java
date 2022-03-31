package org.datavaultplatform.webapp.test;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This SpringMVC controller is used to check values in the ServletContext
 * @See ServletContextTest
 */
@Controller
public class TestServletContextController {

    @GetMapping("/test/context/param")
    @ResponseBody
    String getWebAppRootKey(HttpServletRequest req) {
        String response = req.getServletContext().getInitParameter("webAppRootKey");
        return response;
    }

    @GetMapping("/test/display/name")
    @ResponseBody
    String getDisplayName(HttpServletRequest req) {
        String response = req.getServletContext().getServletContextName();
        return response;
    }

    @GetMapping("/test/session/timeout")
    @ResponseBody
    String getSessionTimeout(HttpServletRequest req) {
        int sessionTimeout = req.getServletContext().getSessionTimeout();
        return String.valueOf(sessionTimeout);
    }
}
