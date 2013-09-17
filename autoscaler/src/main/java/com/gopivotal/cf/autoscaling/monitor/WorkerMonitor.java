package com.gopivotal.cf.autoscaling.monitor;

import com.gopivotal.cf.autoscaling.processmanager.ProcessManager;

public class WorkerMonitor {
    private final ProcessManager processManager;

    public WorkerMonitor(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public int getWorkerStatistics() {
        return this.processManager.getNumWorkers();
    }
}
