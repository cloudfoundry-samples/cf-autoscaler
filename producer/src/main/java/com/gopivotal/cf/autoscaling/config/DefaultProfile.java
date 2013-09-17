package com.gopivotal.cf.autoscaling.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class DefaultProfile {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost", 5672);
    }

}
