package com.cloudrun.webtemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import org.slf4j.MDC;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;



@Controller
public class WebTemplateController {
  // 'spring-cloud-gcp-starter-logging' module provides support for
  // associating a web request trace ID with the corresponding log entries.
  // https://cloud.spring.io/spring-cloud-gcp/multi/multi__stackdriver_logging.html
  private static final Logger logger = LoggerFactory.getLogger(WebTemplateController.class);

  /** Example endpoint handler. */
  /*
  @GetMapping("/")
  //public @ResponseBody String index() {
    // Example of structured logging - add custom fields
    MDC.put("logField", "custom-entry");
    MDC.put("arbitraryField", "custom-entry");
    // Use logger with log correlation
    // https://cloud.google.com/run/docs/logging#correlate-logs
    logger.info("Structured logging example.");
    return "index";
  }
  */

  @GetMapping("/")
	public String greeting(
    @RequestParam(name="name", required=false, defaultValue="World") String name, 
    Model model) {
		
    logger.info("Welcome home page");
    model.addAttribute("name", name);

		return "index";
	}
}
