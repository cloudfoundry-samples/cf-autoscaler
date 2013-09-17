package com.gopivotal.cf.autoscaling.config;

import com.gopivotal.cf.autoscaling.processmanager.CfProcessManager;
import com.gopivotal.cf.autoscaling.processmanager.ProcessManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@Profile("cloud")
@EnableConfigurationProperties({CfSecurityProperties.class, CfProperties.class})
public class CloudProfile {
    Log log = LogFactory.getLog(CloudProfile.class);
    @Autowired
    private CfSecurityProperties securityProperties;
    @Autowired
    private CfProperties cfProperties;

    @Bean
    public CloudFactory cloudFactory() {
        return new CloudFactory();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return cloudFactory().getCloud().getSingletonServiceConnector(ConnectionFactory.class, null);
    }

    @Bean
    public ProcessManager processManager() throws MalformedURLException {
        return new CfProcessManager(cfProperties.getApplicationName(),
                new URL(cfProperties.getTarget()),
                securityProperties.getEmail(),
                securityProperties.getPassword(),
                cfProperties.getOrg(),
                cfProperties.getSpace());
    }

}
