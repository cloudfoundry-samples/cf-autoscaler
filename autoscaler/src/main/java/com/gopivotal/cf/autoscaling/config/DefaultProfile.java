package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.processmanager.MockProcessManager;
import com.gopivotal.cf.autoscaling.processmanager.ProcessManager;
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

    @Bean
    public ProcessManager processManager() {
        return new MockProcessManager();
    }
}
