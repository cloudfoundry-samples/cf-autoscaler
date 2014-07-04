package com.gopivotal.cf.autoscaling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "autoscaler", locations = "classpath:application.yml")
public class AutoscalerProperties {
    private int maxWorkers;
    private int minWorkers;
    private int queueThreshold;
    private int consumerThreshold;
    private int workerDelay;
    private int windowSize;

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public void setMaxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }

    public int getMinWorkers() {
        return minWorkers;
    }

    public void setMinWorkers(int minWorkers) {
        this.minWorkers = minWorkers;
    }

    public int getQueueThreshold() {
        return queueThreshold;
    }

    public void setQueueThreshold(int queueThreshold) {
        this.queueThreshold = queueThreshold;
    }

    public int getConsumerThreshold() {
        return consumerThreshold;
    }

    public void setConsumerThreshold(int consumerThreshold) {
        this.consumerThreshold = consumerThreshold;
    }

    public int getWorkerDelay() {
        return workerDelay;
    }

    public void setWorkerDelay(int workerDelay) {
        this.workerDelay = workerDelay;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
