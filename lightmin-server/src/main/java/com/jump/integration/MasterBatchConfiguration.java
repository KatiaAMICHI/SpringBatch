package com.jump.integration;

import com.jump.configuration.CustomerItemReader;
import com.jump.domain.Asset;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.AggregatorFactoryBean;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
@Slf4j
@EnableBatchProcessing
@EnableBatchIntegration
public class MasterBatchConfiguration {

    private final static String MASTER_JOB_TEST = "JOB_MASTER";
    private final static String MATER_JOB_STEP = "STEP-1";
    private final static int CHUNK_SIZE = 50;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory;

    @Autowired
    @Qualifier("customerItemWriter")
    private ItemWriter<Asset> itemWriter;

    @Bean(MASTER_JOB_TEST)
    public Job processRecordsJob() {
        return jobBuilderFactory
                .get(MASTER_JOB_TEST)
                .flow(managerStep())
                .end()
                .build();
    }

    @Bean("personJob")
    public Job personJob() {
        return jobBuilderFactory
                .get("personJob")
                .start(managerStep())
                .build();
    }

    @Bean
    public Step managerStep() {
        return this.managerStepBuilderFactory
                .get("managerStep")
                .partitioner("workerStep", new BasicPartitioner())
                .gridSize(10)
                .outputChannel(outboundRequests())
                .inputChannel(inboundStaging())
                .build();
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


    @Bean
    public TaskletStep step1() {
        return stepBuilderFactory.get(MATER_JOB_STEP)
                .<Asset, Asset>chunk(CHUNK_SIZE)
                .reader(itemReader())
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean
    @Qualifier("itemReader")
    public ItemReader<Asset> itemReader() {
        log.info(" [Master] .................. ItemReader");
        log.info(" [Master] sleep .................. 9000");
        //Thread.sleep(9000);
        log.info(" [Master] end sleep .................. 9000");
        return new CustomerItemReader("resource");
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("vm://localhost?broker.persistent=false");
        factory.setTrustAllPackages(true);
        return factory;
    }

}