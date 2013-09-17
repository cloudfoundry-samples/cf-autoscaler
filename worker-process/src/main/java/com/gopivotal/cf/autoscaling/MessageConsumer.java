package com.gopivotal.cf.autoscaling;

public class MessageConsumer {

    private final int delay;

    public MessageConsumer(int delay) {
        this.delay = delay;
    }

    public void handleWorkPackage(String content) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
        }
        System.out.println("Processed '" + content + "'");
    }
}
