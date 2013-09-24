package com.gopivotal.cf.autoscaling.monitor;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ResponseTimeMonitor {




    public ResponseTimeStats getResponseTimeStatistics() {
//        return this.amqpTemplate.execute(new ChannelCallback<ResponseTimeStats>() {
//            public ResponseTimeStats doInRabbit(Channel channel) throws java.io.IOException {
//                AMQP.Queue.DeclareOk queueInfo = channel.queueDeclarePassive(queueName);
//                return new ResponseTimeStats(queueInfo.getMessageCount(), queueInfo.getConsumerCount());
//            }
//        });

        // Poll the endpoint exposing average response time

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        Double number = restTemplate.getForObject("http://helloflask25.cfapps.io/number", Double.class);



        // Create an instance of ResponseTimeStats w/ the data
        return new ResponseTimeStats(number);

        // return it
    }

    public static class ResponseTimeStats {
        public double time;

        public ResponseTimeStats(double time) {
            this.time = time;
        }
    }
}
