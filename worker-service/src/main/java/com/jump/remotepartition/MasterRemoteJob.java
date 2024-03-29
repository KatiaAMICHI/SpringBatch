package com.jump.remotepartition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
//@Profile("master")
public class MasterRemoteJob {
    static String TOPIC = "step-execution-eventslol";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RemotePartitioningManagerStepBuilderFactory remotePartitioningManager;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private JobRepository jobRepository;

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow() {
        final KafkaProducerMessageHandler kafkaMessageHandler = new KafkaProducerMessageHandler(kafkaTemplate);
        kafkaMessageHandler.setTopicExpression(new LiteralExpression(TOPIC));
        return IntegrationFlows
                .from(requests())
                .handle(kafkaMessageHandler)
                .get();
    }

    @Bean("job_partition_master")
    public Job job() throws Exception {
        return jobBuilderFactory.get("job_partition_master")
                                .start(masterStep())
                                .build();
    }

    @Bean
    public Step masterStep() throws Exception {
        return remotePartitioningManager
                .get("step1_manager")
                .partitioner("step1_worker", new BasicPartitioner()).gridSize(1)
                .repository(jobRepository)
                .outputChannel(requests())
                .build();
    }
}
