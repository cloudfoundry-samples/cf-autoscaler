package com.gopivotal.cf.autoscaling;

import com.gopivotal.cf.autoscaling.monitor.QueueMonitor;
import com.gopivotal.cf.autoscaling.processmanager.ProcessManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Autoscaler {
    // Configuration
    private final int maxWorkers;    // max number of worker processes
    private final int minWorkers;    // min number of worker processes
    private final int queueThreshold;      // if queue grows above this add workers
    private final int consumerThreshold;   // if num available consumers grows above this, remove workers
    private final int workerDelay;   // grace period (seconds) after adding or removing a worker
    private final int windowSize;    // number of consecutive samples above threshold before action is taken
    private final ProcessManager processManager;
    Log log = LogFactory.getLog(this.getClass());
    // Working state
    private long lastWorkerAction = 0L;
    private int consecutiveQueueThresholdSamples = 0;
    private int consecutiveConsumerThresholdSamples = 0;
    private int numWorkers;

    public Autoscaler(int maxWorkers, int minWorkers,
                      int queueThreshold, int consumerThreshold,
                      int workerDelay, int windowSize,
                      ProcessManager processManager) {
        this.maxWorkers = maxWorkers;
        this.minWorkers = minWorkers;
        this.queueThreshold = queueThreshold;
        this.consumerThreshold = consumerThreshold;
        this.workerDelay = workerDelay;
        this.windowSize = windowSize;
        this.processManager = processManager;
    }

    public void onQueueStats(QueueMonitor.QueueStats stats) {
        log.info("{ msgs : " + stats.size + " consumers : " + stats.consumers + "}");
        // don't take action on stats within grace period
        if (!inGracePeriod()) {
            sampleQueueSize(stats.size);
            sampleConsumers(stats.consumers);

            if (moreWorkersRequired()) {
                scaleUp();
            }
            if (lessWorkersRequired()) {
                scaleDown();
            }
        }
    }

    public void onWorkerStats(int numWorkers) {
        log.info("{ workers : " + numWorkers + "}");
        this.numWorkers = numWorkers;
        if (numWorkers < this.minWorkers) {
            scaleTo(this.minWorkers);
        }
    }

    private boolean inGracePeriod() {
        long actionGap = System.currentTimeMillis() - lastWorkerAction;
        int workerDelay = this.workerDelay * 1000;
        log.debug("inGracePeriod if " + actionGap + " < " + workerDelay);
        return actionGap < workerDelay;
    }

    private void sampleQueueSize(int msgs) {
        if (msgs > this.queueThreshold) {
            this.consecutiveQueueThresholdSamples += 1;
        } else {
            this.consecutiveQueueThresholdSamples = 0;
        }
    }

    private void sampleConsumers(int consumers) {
        if (consumers > this.consumerThreshold) {
            this.consecutiveConsumerThresholdSamples += 1;
        } else {
            this.consecutiveConsumerThresholdSamples = 0;
        }
    }

    private boolean moreWorkersRequired() {
        return (numWorkers < this.maxWorkers) &&
                (this.consecutiveQueueThresholdSamples > windowSize);
    }

    private boolean lessWorkersRequired() {
        return (numWorkers > this.minWorkers) &&
                (this.consecutiveConsumerThresholdSamples > windowSize);
    }

    private void scaleUp() {
        this.processManager.addWorkerProcess();
        this.lastWorkerAction = System.currentTimeMillis();
        resetCounts();
    }

    private void scaleDown() {
        this.processManager.removeWorkerProcess();
        this.lastWorkerAction = System.currentTimeMillis();
        resetCounts();
    }

    private void scaleTo(int numWorkers) {
        this.processManager.scaleTo(numWorkers);
        this.lastWorkerAction = System.currentTimeMillis();
        resetCounts();
    }

    private void resetCounts() {
        this.consecutiveQueueThresholdSamples = 0;
        this.consecutiveConsumerThresholdSamples = 0;
    }
}
