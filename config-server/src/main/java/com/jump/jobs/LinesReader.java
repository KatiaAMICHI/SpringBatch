package com.jump.jobs;

import com.jump.objects.JobEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@EnableBinding(Source.class)
@Slf4j
@AllArgsConstructor
public class LinesReader implements Tasklet, StepExecutionListener {
    @Autowired
    private final Source source;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.debug("Lines Reader initialized.");

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        String value = "data";
        System.out.println("Sending: " + value);

        final Map<String, Object> parameters =
                stepContribution.getStepExecution().getJobParameters().getParameters()
                                .entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));

        final String path = "http://localhost:8888/asset";

        final JobEvent payload = new JobEvent(parameters, path);
        final Message<JobEvent> partitionKey = MessageBuilder.withPayload(payload)
                                                             .setHeader("partitionKey", payload)
                                                             .build();
        this.source.output().send(partitionKey);

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.debug("Lines Reader ended.");
        return ExitStatus.COMPLETED;
    }

    @InboundChannelAdapter(channel = Source.OUTPUT, autoStartup= "false")
    public Message<?> generate() {
        String value = "data";
        System.out.println("Sending: " + value);

        final Message<String> partitionKey = MessageBuilder.withPayload(value)
                                                           .setHeader("partitionKey", value)
                                                           .build();
        this.source.output().send(partitionKey);

        return partitionKey;
    }

}