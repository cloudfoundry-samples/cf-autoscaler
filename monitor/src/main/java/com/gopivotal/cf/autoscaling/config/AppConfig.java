package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.StatsHandler;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.cloud.cloudfoundry.CloudFoundryConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.socket.WebSocketHandler;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(RabbitProperties.class)
@ComponentScan
@ImportResource("integration-context.xml")
public class AppConfig extends SpringBootServletInitializer {

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

    @Override
    protected Class<?> getConfigClass() {
        return AppConfig.class;
    }

    @Bean
    public RabbitTemplate amqpTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean(name = "/stats")
    public WebSocketHandler statsHandler() {
        return new StatsHandler();
    }

    @Bean
    public TopicExchange monitoringExchange() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        Queue queue = new Queue(rabbitProperties.getQueue());
        TopicExchange exchange = new TopicExchange(rabbitProperties.getExchange());

        admin.declareQueue(queue);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("#"));

        return exchange;
    }
}
