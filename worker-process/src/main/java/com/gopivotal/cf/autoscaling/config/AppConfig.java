package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.MessageConsumer;
import com.rabbitmq.client.Channel;
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
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        admin.getRabbitTemplate().execute(new ChannelCallback<Object>() {
            @Override
            public Object doInRabbit(Channel channel) throws Exception {
                channel.queueDeclarePassive(rabbitProperties.getQueue());
                channel.exchangeDeclarePassive(rabbitProperties.getExchange());
                channel.queueBind(rabbitProperties.getQueue(), rabbitProperties.getExchange(), rabbitProperties.getKey());
                return null;
            }
        });

        return admin;
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
