package com.jump.jobs.object.asset;

import com.jump.objects.JobEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class AssetTasklet implements Tasklet, StepExecutionListener {
    private final Source source;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.debug("Assettasket initialized");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        // soucis avec la deserializing du JobParameter quand le worker reçoit le message
        final Map<String, Object> parameters =
                stepContribution.getStepExecution().getJobParameters().getParameters()
                                .entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
        long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobId();
        final BatchStatus status = chunkContext.getStepContext().getStepExecution().getJobExecution().getStatus();

        final Long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecution().getId();
        final String path = "http://localhost:1111/asset/get?label=" + parameters.get("value");

        final JobEvent payload = new JobEvent(jobId, jobExecutionId, parameters, path, status, "UNKNOWN");
        final Message<JobEvent> partitionKey = MessageBuilder.withPayload(payload)
                                                             .setHeader("partitionKey", payload)
                                                             .build();
        source.output().send(partitionKey);

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.debug("Job executing in remote server");
        return ExitStatus.UNKNOWN;
    }

}