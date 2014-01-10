package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.MessageProducer;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.cloudfoundry.CloudFoundryConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(RabbitProperties.class)
@ComponentScan
@ImportResource("integration-context.xml")
public class AppConfig {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private RabbitProperties rabbitProperties;

    public static void main(String[] args) {
        if (new CloudFoundryConnector().isInMatchingCloud()) {
            System.setProperty("spring.profiles.active", "cloud");
        }
        SpringApplication.run(AppConfig.class, args);
    }

    @Bean
    public RabbitTemplate amqpTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public TopicExchange workExchange() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        TopicExchange exchange = new TopicExchange(rabbitProperties.getExchange());

        admin.declareExchange(exchange);

        return exchange;
    }

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }
}
