package com.jump;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configs {


    /*@Bean
    public java.util.function.Consumer<Message<?>> process() {
        return message -> {
            Consumer<?, ?> consumer = message.getHeaders().get(KafkaHeaders.CONSUMER, Consumer.class);
            String topic = message.getHeaders().get(KafkaHeaders.TOPIC, String.class);
            Integer partitionId = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class);
            consumer.pause(Collections.singleton(new TopicPartition(topic, partitionId)));
        };
    }*/

}
