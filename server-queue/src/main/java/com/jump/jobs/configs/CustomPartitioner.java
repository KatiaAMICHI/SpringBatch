package com.jump.jobs.configs;

import com.jump.objects.jobObject.JobEvent;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.springframework.context.annotation.Configuration;

/**
 * si on souhaite avoir changer le comportement du partitionnement par defaut de kafka
 */
@Configuration
public class CustomPartitioner extends DefaultPartitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String partitionKey = null;
        if (topic.equals("workern") && null != value && value instanceof JobEvent) {
            final JobEvent jobEvent = (JobEvent) value;
            partitionKey = String.valueOf(jobEvent.getChannelPartition());
            keyBytes = partitionKey.getBytes();
        }
        return super.partition(topic, partitionKey, keyBytes, value, valueBytes, cluster);
    }

    @Override
    public void onNewBatch(String topic, Cluster cluster, int prevPartition) {
        super.onNewBatch(topic, cluster, prevPartition);
    }
}