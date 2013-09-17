package com.gopivotal.cf.autoscaling.monitor;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class QueueMonitor {
    private final String queueName;
    private final RabbitTemplate amqpTemplate;

    public QueueMonitor(String queueName, RabbitTemplate template) {
        this.queueName = queueName;
        this.amqpTemplate = template;
    }

    public QueueStats getQueueStatistics() {
        return this.amqpTemplate.execute(new ChannelCallback<QueueStats>() {
            public QueueStats doInRabbit(Channel channel) throws java.io.IOException {
                AMQP.Queue.DeclareOk queueInfo = channel.queueDeclarePassive(queueName);
                return new QueueStats(queueInfo.getMessageCount(), queueInfo.getConsumerCount());
            }
        });
    }

    public static class QueueStats {
        public int size;
        public int consumers;

        public QueueStats(int s, int c) {
            this.size = s;
            this.consumers = c;
        }
    }
}
