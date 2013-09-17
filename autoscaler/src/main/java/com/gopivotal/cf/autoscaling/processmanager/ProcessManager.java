package com.gopivotal.cf.autoscaling.processmanager;

public interface ProcessManager {
    public int getNumWorkers();

    public void addWorkerProcess();

    public void removeWorkerProcess();

    public void scaleTo(int numWorkers);
}
