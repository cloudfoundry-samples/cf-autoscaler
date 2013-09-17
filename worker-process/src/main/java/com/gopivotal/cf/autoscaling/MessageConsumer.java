package com.gopivotal.cf.autoscaling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageConsumer {

    private final int delay;
    private Log log = LogFactory.getLog(MessageConsumer.class);

    public MessageConsumer(int delay) {
        this.delay = delay;
    }

    public void handleWorkPackage(String content) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
        }

        log.info("Processed '" + content + "'");
    }
}
