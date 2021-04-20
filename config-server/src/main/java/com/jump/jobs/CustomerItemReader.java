package com.jump.jobs;

import com.jump.objects.JobEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomerItemReader implements ItemReader<JobEvent> {
    private final String jobName;
    private final JobBuilderFactory stepBuilders;
    private final Source sources;


    private ItemReader<JobEvent> delegate;

    @Override
    public JobEvent read() throws Exception {
        final Message<JobEvent> partitionKey = getMsgTosend(null, null);

        sources.output().send(partitionKey);

        if (delegate == null) {
            delegate = new IteratorItemReader<JobEvent>(Collections.emptyIterator());
        }
        return delegate.read();
    }

    public Message<JobEvent> getMsgTosend(StepContribution stepContribution, ChunkContext chunkContext) {
        JobEvent payload;
        if (null == stepContribution || null == chunkContext) {
            payload = new JobEvent();
        } else {
            // soucis avec la deserializing du JobParameter quand le worker reçoit le message
            final Map<String, Object> parameters =
                    stepContribution.getStepExecution().getJobParameters().getParameters()
                                    .entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
            long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobId();
            final BatchStatus status = chunkContext.getStepContext().getStepExecution().getJobExecution().getStatus();

            final String path = "http://localhost:1111/asset/get?label=" + parameters.get("value");
            final Long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecution().getId();

            payload = new JobEvent(jobId, jobExecutionId, parameters, path, status, "UNKNOWN");
        }


        final Message<JobEvent> partitionKey = MessageBuilder.withPayload(payload)
                                                             .setHeader("partitionKey", payload)
                                                             .build();

        return partitionKey;

    }

}