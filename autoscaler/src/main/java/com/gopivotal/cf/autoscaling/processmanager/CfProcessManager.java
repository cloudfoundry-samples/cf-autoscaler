package com.gopivotal.cf.autoscaling.processmanager;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.domain.CloudApplication;

import javax.annotation.PreDestroy;
import java.net.URL;

public class CfProcessManager implements ProcessManager {
    private final String cfAppName;
    private final CloudFoundryOperations cfOps;

    public CfProcessManager(String appName, URL targetUrl, String username, String password, String orgName, String spaceName) {
        System.out.println("CfProcessManager connecting to " + targetUrl + " as " + username);
        this.cfAppName = appName;

        // login to Cloud Foundry
        CloudCredentials credentials = new CloudCredentials(username, password);
        this.cfOps = new CloudFoundryClient(credentials, targetUrl, orgName, spaceName);
        this.cfOps.login();

        // ensure the process we are monitoring is started
        CloudApplication app = this.cfOps.getApplication(this.cfAppName);
        if (app.getState() == CloudApplication.AppState.STOPPED) {
            this.cfOps.startApplication(this.cfAppName);
        }
    }

    public int getNumWorkers() {
        CloudApplication application = this.cfOps.getApplication(this.cfAppName);
        return application.getRunningInstances();
    }

    public void addWorkerProcess() {
        int currentWorkers = getNumWorkers();
        this.cfOps.updateApplicationInstances(this.cfAppName, currentWorkers + 1);
    }

    public void removeWorkerProcess() {
        int currentWorkers = getNumWorkers();
        if (currentWorkers > 0) {
            this.cfOps.updateApplicationInstances(this.cfAppName, currentWorkers - 1);
        }
    }

    public void scaleTo(int numWorkers) {
        this.cfOps.updateApplicationInstances(this.cfAppName, numWorkers);
    }

    @PreDestroy
    public void logout() {
        this.cfOps.logout();
    }
}
