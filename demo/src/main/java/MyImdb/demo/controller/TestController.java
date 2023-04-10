package MyImdb.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/testEndpoint")
    public String testEndpoint(){
        logger.info("Log Test");
        return "testEndpoint";
    }

    @GetMapping("/helloWorld")
    public String helloWorld(){
        logger.trace("TRACE Log helloWorld");
        logger.info("INFO Log helloWorld");
        return "hello World";
    }
}
