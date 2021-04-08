package com.jump.jobs;

import com.jump.objects.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@EnableBinding(Source.class)
public class AssetJobConfiguration {
  @Autowired
  public JobBuilderFactory jobBuilderFactory;
  @Autowired
  public StepBuilderFactory stepBuilderFactory;
  @Autowired
  private Source sources;

  @Bean @Qualifier("jobasset")
  public Job job() {
    return jobBuilderFactory
            .get("jobasset")
            .start(workerStep())
            .build();
  }

    @Bean
    public Step workerStep() {
        return this.stepBuilderFactory
                .get("jobStep1")
                .tasklet(workerTask(sources))
                .build();
    }

    @Bean
    public Tasklet workerTask(final Source source) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

                // soucis avec la deserializing du JobParameter quand le worker reçoit le message
                final Map<String, Object> parameters =
                        stepContribution.getStepExecution().getJobParameters().getParameters()
                                        .entrySet().stream()
                                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));
                long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobId();
                final BatchStatus status = chunkContext.getStepContext().getStepExecution().getJobExecution().getStatus();

                final String path = "http://localhost:1111/asset/get?label=" + parameters.get("value");

                final JobEvent payload = new JobEvent(jobId, parameters, path, status);
                final Message<JobEvent> partitionKey = MessageBuilder.withPayload(payload)
                                                                     .setHeader("partitionKey", payload)
                                                                     .build();
                source.output().send(partitionKey);

                return RepeatStatus.CONTINUABLE;
            }
        };
    }

}