package com.jump.integration;

import com.jump.domain.Asset;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.AggregatorFactoryBean;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

//@EnableBatchIntegration
//@EnableLightminEmbedded
@Slf4j
public class RemoteChunkingJobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobExplorer jobExplorer;

    @Bean
    public org.apache.activemq.ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("http://localhost:9012/lightmin-client");
        return factory;
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public static DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
                .get();
    }

    /*partitionHandler
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public static QueueChannel replies() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
                .channel(replies())
                .get();
    }

    /*
     * Configure the ChunkMessageChannelItemWriter
     */
    @Bean
    public static ItemWriter<Asset> itemWriter() {
        log.info(".................. ItemReader");
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.setDefaultChannel(requests());
        messagingTemplate.setReceiveTimeout(2000);
        ChunkMessageChannelItemWriter<Asset> chunkMessageChannelItemWriter
                = new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate);
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }


    /* @Configuration
    public static class ManagerConfiguration {

        @Autowired
        private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;
        @Autowired
        @Qualifier("itemReader")
        private ItemReader<Asset> itemReader;

        @Bean
        public TaskletStep managerStep() {
            return this.managerStepBuilderFactory.get("managerStep")
                       .chunk(100)
                       .reader(itemReader)
                       .outputChannel(requests()) // requests sent to workers
                       .inputChannel(replies())   // replies received from workers
                       .build();
        }

        // Middleware beans setup omitted

    }*/

    /*
     * Configuration of the manager side
     */
    @Bean
    public PartitionHandler partitionHandler() {
        MessageChannelPartitionHandler partitionHandler = new MessageChannelPartitionHandler();
        partitionHandler.setStepName("step1");
        partitionHandler.setGridSize(3);
        partitionHandler.setReplyChannel(outboundReplies());
        MessagingTemplate template = new MessagingTemplate();
        template.setDefaultChannel(outboundRequests());
        template.setReceiveTimeout(100000);
        partitionHandler.setMessagingOperations(template);
        return partitionHandler;
    }

    @Bean
    public QueueChannel outboundReplies() {
        return new QueueChannel();
    }

    @Bean
    public DirectChannel outboundRequests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundJmsRequests() {
        return IntegrationFlows.from("outboundRequests")
                               .handle(Jms.outboundGateway(connectionFactory())
                                          .requestDestination("requestsQueue"))
                               .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "inboundStaging")
    public AggregatorFactoryBean partitioningMessageHandler() throws Exception {
        AggregatorFactoryBean aggregatorFactoryBean = new AggregatorFactoryBean();
        aggregatorFactoryBean.setProcessorBean(partitionHandler());
        aggregatorFactoryBean.setOutputChannel(outboundReplies());
        // configure other propeties of the aggregatorFactoryBean
        return aggregatorFactoryBean;
    }

    @Bean
    public DirectChannel inboundStaging() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundJmsStaging() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory())
                         .configureListenerContainer(c -> c.subscriptionDurable(false))
                         .destination("stagingQueue"))
                .channel(inboundStaging())
                .get();
    }

    /*
     * Configuration of the worker side
     */
    @Bean
    public StepExecutionRequestHandler stepExecutionRequestHandler() {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        stepExecutionRequestHandler.setJobExplorer(jobExplorer);
        // stepExecutionRequestHandler.setStepLocator(stepLocator());
        return stepExecutionRequestHandler;
    }

    @Bean
    @ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundStaging")
    public StepExecutionRequestHandler serviceActivator() throws Exception {
        return stepExecutionRequestHandler();
    }

    @Bean
    public DirectChannel inboundRequests() {
        return new DirectChannel();
    }

    public IntegrationFlow inboundJmsRequests() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory())
                         .configureListenerContainer(c -> c.subscriptionDurable(false))
                         .destination("requestsQueue"))
                .channel(inboundRequests())
                .get();
    }

    @Bean
    public DirectChannel outboundStaging() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundJmsStaging() {
        return IntegrationFlows.from("outboundStaging")
                               .handle(Jms.outboundGateway(connectionFactory())
                                          .requestDestination("stagingQueue"))
                               .get();
    }

    public Job personJob() {
        return jobBuilderFactory.get("personJob")
                                .start(stepBuilderFactory.get("step1.manager")
                                                         .partitioner("step1.worker", new BasicPartitioner())
                                                         .partitionHandler(partitionHandler())
                                                         .build())
                                .build();
    }
}