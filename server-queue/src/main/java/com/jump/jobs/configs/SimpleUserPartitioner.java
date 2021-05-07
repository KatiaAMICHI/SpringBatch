package com.jump.jobs.configs;

import com.jump.objects.jobObject.JobEvent;
import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SimpleUserPartitioner implements PartitionKeyExtractorStrategy {

	@Override
	public Object extractKey(Message<?> message) {
		if(message.getPayload() instanceof JobEvent) {
			JobEvent jobEvent = (JobEvent) message.getPayload();
			return jobEvent.getChannelPartition();
		}
		return 10;
	}
    
}