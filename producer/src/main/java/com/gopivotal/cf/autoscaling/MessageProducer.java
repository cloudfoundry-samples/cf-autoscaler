package com.gopivotal.cf.autoscaling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageProducer {
    Log log = LogFactory.getLog(MessageProducer.class);
    private int messageCounter = 0;

    public String createWorkPackage() {
        String payload = "This is message #" + ++messageCounter;
        log.info(payload);
        return payload;
    }
}
