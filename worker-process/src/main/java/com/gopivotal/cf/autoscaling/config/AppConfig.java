package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.MessageConsumer;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.cloudfoundry.CloudFoundryConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(RabbitProperties.class)
@ComponentScan
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
    public Queue workQueue() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        Queue queue = new Queue(rabbitProperties.getQueue());
        TopicExchange exchange = new TopicExchange(rabbitProperties.getExchange());

        admin.declareQueue(queue);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(rabbitProperties.getKey()));

        return queue;
    }

    @Bean
    public MessageConsumer worker() {
        return new MessageConsumer(rabbitProperties.getWorkerDelay());
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setPrefetchCount(3);

        MessageListenerAdapter adapter = new MessageListenerAdapter(worker());
        adapter.setDefaultListenerMethod("handleWorkPackage");

        container.setMessageListener(adapter);
        container.setQueueNames(rabbitProperties.getQueue());
        container.start();

        return container;
    }

}
