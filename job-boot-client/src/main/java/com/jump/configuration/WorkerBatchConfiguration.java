package com.jump.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
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
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;

    @Value("${broker.url}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(this.brokerUrl);
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    /*
     * Configure inbound flow (requests coming from the master)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
                .channel(requests())
                .get();
    }

    /*
     * Configure outbound flow (replies going to the master)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(replies())
                .handle(Jms.outboundAdapter(connectionFactory).destination("replies"))
                .get();
    }

    /*
     * Configure the worker step
     */
    @Bean
    public Step workerStep() {
        return this.workerStepBuilderFactory.get("workerStep")
                                            .inputChannel(requests())
                                            .outputChannel(replies())
                                            .tasklet(tasklet(null))
                                            .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{stepExecutionContext['partition']}") String partition) {
        return (contribution, chunkContext) -> {
            System.out.println("processing " + partition);
            log.info("[Worker]... processing " + partition);
            return RepeatStatus.FINISHED;
        };
    }
    /*
    @StepScope
    @Bean
    @Qualifier("itemReader")
    public ItemReader<Asset> itemReader() {
        log.info("[Worker].................. ItemReader");
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
*/

}