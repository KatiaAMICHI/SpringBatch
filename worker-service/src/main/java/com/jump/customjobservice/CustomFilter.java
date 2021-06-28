package com.jump.customjobservice;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.MetricName;
import org.springframework.stereotype.Service;

@Service
public class CustomFilter {

    public Boolean stratfun(String locV, KafkaConsumer parConsummer) {
        final MetricName next = (MetricName)parConsummer.metrics().keySet().iterator().next();
        next.tags().get("client-id");

        return false;
    }

    public Boolean stratfun(String locV, String parConsummer) {
        return locV.equals("star");
    }

    public Boolean stratfun(KafkaConsumer parConsummer) {
        return parConsummer.equals("star");
    }
}
