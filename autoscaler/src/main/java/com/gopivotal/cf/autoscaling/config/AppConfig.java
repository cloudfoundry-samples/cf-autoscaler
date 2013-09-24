package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.Autoscaler;
import com.gopivotal.cf.autoscaling.monitor.QueueMonitor;
import com.gopivotal.cf.autoscaling.monitor.ResponseTimeMonitor;
import com.gopivotal.cf.autoscaling.monitor.WorkerMonitor;
import com.gopivotal.cf.autoscaling.processmanager.ProcessManager;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
@EnableConfigurationProperties({AutoscalerProperties.class, RabbitProperties.class})
@ComponentScan
@ImportResource("integration-context.xml")
public class AppConfig {

    @Autowired
    AutoscalerProperties autoscalerProperties;
    @Autowired
    RabbitProperties rabbitProperties;
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private ProcessManager processManager;

    public static void main(String[] args) {
        if (new CloudFoundryConnector().isInMatchingCloud()) {
            System.setProperty("spring.profiles.active", "cloud");
        }
        SpringApplication.run(AppConfig.class, args);
    }

    //Kill
    @Bean
    public RabbitTemplate amqpTemplate() {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public QueueMonitor queueMonitor() {
        return new QueueMonitor(rabbitProperties.getQueue(), amqpTemplate());
    }

    @Bean
    public WorkerMonitor workerMonitor() {
        return new WorkerMonitor(processManager);
    }

    @Bean
    public ResponseTimeMonitor responseTimeMonitor() {
        return new ResponseTimeMonitor();
    }

    @Bean
    public Autoscaler autoscaler() {
        return new Autoscaler(autoscalerProperties.getMaxWorkers(),
                autoscalerProperties.getMinWorkers(),
                autoscalerProperties.getQueueThreshold(),
                autoscalerProperties.getConsumerThreshold(),
                autoscalerProperties.getWorkerDelay(),
                autoscalerProperties.getWindowSize(),
                processManager);
    }

    //Kill
    @Bean
    public TopicExchange monitoringExchange() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        TopicExchange exchange = new TopicExchange(rabbitProperties.getExchange());
        admin.declareExchange(exchange);
        return exchange;
    }
}
