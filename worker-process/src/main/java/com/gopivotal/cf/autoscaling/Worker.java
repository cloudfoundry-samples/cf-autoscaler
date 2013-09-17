package com.gopivotal.cf.autoscaling;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource("integration-context.xml")
public class Worker {

    public static void main(String[] args) {
        boolean runningInCloudFoundry = new CloudEnvironment().isCloudFoundry();

        String runningIn = runningInCloudFoundry ? "in Cloud Foundry" : "Locally";
        System.out.println("Hello from worker running " + runningIn);

        if (runningInCloudFoundry) {
            // activate the cloud profile - we do this explicitly because we are
            // deploying as a standalone app (not a spring web app)
            System.setProperty("spring.profiles.active", "cloud");
        }

        SpringApplication.run(Worker.class, args);
    }

}
