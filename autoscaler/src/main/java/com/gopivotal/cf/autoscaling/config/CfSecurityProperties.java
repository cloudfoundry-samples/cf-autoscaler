package com.gopivotal.cf.autoscaling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(path = "classpath:cf-security.yml")
public class CfSecurityProperties {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
