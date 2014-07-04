package com.gopivotal.cf.autoscaling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit", locations = "classpath:application.yml")
public class RabbitProperties {
    private String queue;
    private String exchange;
    private String key;
    private int workerDelay;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getWorkerDelay() {
        return workerDelay;
    }

    public void setWorkerDelay(int workerDelay) {
        this.workerDelay = workerDelay;
    }
}
