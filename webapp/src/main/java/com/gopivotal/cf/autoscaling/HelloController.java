package com.gopivotal.cf.autoscaling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@Controller
@EnableAutoConfiguration
public class HelloController {

    private Log log = LogFactory.getLog(HelloController.class);
    @Value("${maxDelay}")
    private int maxDelay;
    @Value("${seed}")
    private int seed;

    @Autowired
    private Random random;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HelloController.class, args);
    }

    @Bean
    public Random random() {
        return new Random(seed);
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        int delay = random.nextInt(maxDelay);
        log.info("Sleeping for: " + delay);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
        log.info("Servicing request after sleep.");
        return "Hello World!";
    }
}
