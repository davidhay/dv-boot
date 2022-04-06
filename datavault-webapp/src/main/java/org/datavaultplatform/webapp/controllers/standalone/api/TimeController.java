package org.datavaultplatform.webapp.controllers.standalone.api;

import java.time.Clock;
import java.time.LocalDateTime;
import org.datavaultplatform.test.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TimeController {

  private final Clock clock;

  @Autowired
  public TimeController(Clock clock){
    this.clock = clock;
  }
  
  @ResponseBody
  @RequestMapping("/time")
  public Time getTime(){
      return new Time(LocalDateTime.now(clock));
  }
}
