package com.jump.configuration;

import com.jump.domain.Asset;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;


@Configuration
@IntegrationComponentScan
@EnableIntegration
@EnableBatchIntegration
@Slf4j
public class WorkerBatchConfiguration {

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;

    @Bean
    public Step workerStep() {
        return this.workerStepBuilderFactory
                .get("workerStep")
                .inputChannel(inboundRequests())
                .outputChannel(outboundStaging())
                .chunk(100)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public StepExecutionRequestHandler stepExecutionRequestHandler() {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        stepExecutionRequestHandler.setJobExplorer(jobExplorer);
        stepExecutionRequestHandler.setStepLocator(new BeanFactoryStepLocator());
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

    @Bean
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


    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("vm://localhost?broker.persistent=false");
        factory.setTrustAllPackages(true);
        return factory;
    }

    /*
     * Configure inbound flow (requests going to workers)
     */
    @Bean
    public QueueChannel replies() {
        return new QueueChannel();
    }

    @StepScope
    @Bean
    @Qualifier("itemReader")
    public ItemReader<Asset> itemReader() {
        log.info(".................. ItemReader");
        log.info("sleep .................. 9000");
        //Thread.sleep(9000);
        log.info("end sleep .................. 9000");
        return new CustomerItemReader("resource");
    }

    @Bean
    @Qualifier("itemWriter")
    public ItemWriter<? super Object> itemWriter() {
        log.info(".................. itemWriter");
        return assets -> {
            if (assets != null && !assets.isEmpty()) {
                log.info("Write: {}", assets);
                // assetKVRepository.saveAll(assets);
            }
        };
    }

    @Bean
    @Qualifier("itemProcessor")
    public ItemProcessor<? super Object, ?> itemProcessor() {
        log.info(".................. itemProcessor");
        return new ItemProcessor<Object, Object>() {
            @Override
            public Object process(Object o) {
                //asset.setLabel("newLabel");
                log.info("Pause for 6 seconds");
                //Thread.sleep(6000);
                return o;
            }
        };
    }



}