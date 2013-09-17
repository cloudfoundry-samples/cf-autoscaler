package com.gopivotal.cf.autoscaling.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class CloudProfile {

    @Bean
    public CloudFactory cloudFactory() {
        return new CloudFactory();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return cloudFactory().getCloud().getSingletonServiceConnector(ConnectionFactory.class, null);
    }

}
