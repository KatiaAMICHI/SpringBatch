package com.jump.remotepartition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@Profile("worker11")
public class WorkerRemoteJob {
    static String TOPIC = "step-execution-eventslol";
    static String GROUP_ID = "stepresponse_partition";

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
    @Autowired
    private ConsumerFactory kafkaFactory;

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow() {
        final ContainerProperties containerProps = new ContainerProperties(TOPIC);
        containerProps.setGroupId(GROUP_ID);

        final KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(kafkaFactory, containerProps);
        final KafkaMessageDrivenChannelAdapter kafkaMessageChannel = new KafkaMessageDrivenChannelAdapter(container);

        return IntegrationFlows
                .from(kafkaMessageChannel)
                .channel(requests())
                .get();
    }

    @Bean("step1_worker")
    public Step workerStep() {
        final TaskExecutorRepeatTemplate repeatTemplate = new TaskExecutorRepeatTemplate();
        repeatTemplate.setThrottleLimit(1);
        return workerStepBuilderFactory
                .get("step1_worker")
                .inputChannel(requests())
                .repository(jobRepository)
                .tasklet(tasklet(null))
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .stepOperations(repeatTemplate)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{stepExecutionContext['partition']}") final String partition) {
        return (contribution, chunkContext) -> {
            log.info("[ MasterRemoteJob ] remote tasklet , partition : " + partition);
            Thread.sleep(20000);
            return RepeatStatus.FINISHED;
        };
    }

}
