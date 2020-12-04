package com.jump.configuration;

import com.jump.domain.Asset;
import com.jump.integration.BasicPartitioner;
import com.jump.queue.InterceptingJobExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import org.tuxdevelop.spring.batch.lightmin.annotation.EnableLightminEmbedded;

/*@Configuration
@Slf4j
@EnableLightminEmbedded*/
public class CustomerJob1 { } /*
    @Autowired
    private JobExplorer jobExplorer;

    @Qualifier("outboundRequest")
    @Autowired
    private MessageChannel outboundRequest;
    @Autowired @Qualifier("customerItemProcessor")
    private ItemProcessor<Asset, Asset> assetItemProcessor;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("itemReader")
    private ItemReader<Asset> assetItemReader;
    @Qualifier("itemWriter")
    @Autowired
    private ItemWriter<Asset> assetItemWriter;

    @Bean("assetJob")
    public Job chunkJob() throws Exception {
        return jobBuilderFactory
                .get("assetJob")
                .flow(combineReleaseJobCL31401())
                .from(combineReleaseJobCL31401()).on("N").to(combineReleaseJobNormalStepManager())
                .from(combineReleaseJobCL31401()).on("R").end()
                .from(combineReleaseJobNormalStepManager()).on("FAILED").fail()
                .end().start(stepBuilderFactory.get("step1")
                        .<Asset, Asset>chunk(200)
                        .reader(assetItemReader)
                        .writer(assetItemWriter)
                        .build())
                .end().incrementer(new RunIdIncrementer())
                .listener(new InterceptingJobExecution())
                .build();
    }
    @Bean
    public Step combineReleaseJobCL31401() {
        return stepBuilderFactory
                .get("customerStep")
                .<Asset, Asset>chunk(1)
                .reader(assetItemReader)
                .processor(assetItemProcessor)
                .writer(assetItemWriter)
                .build();
    }

    @Bean
    public Step combineReleaseJobNormalStepManager() throws Exception {
        return stepBuilderFactory.get("combineReleaseJobNormalStep.Manager")
                                 .partitioner("combineReleaseJobNormalStep", new BasicPartitioner())
                                 .partitionHandler(partitionHandler())
                                 .build();
    }


    @Bean
    public PartitionHandler partitionHandler() throws Exception {
        MessageChannelPartitionHandler partitionHandler = new MessageChannelPartitionHandler();

        partitionHandler.setStepName("combineReleaseJobNormalStep");
        partitionHandler.setGridSize(10);
        partitionHandler.setMessagingOperations(messageTemplate());
        //partitionHandler.setPollInterval(5000l);
        partitionHandler.setJobExplorer(jobExplorer);

        partitionHandler.afterPropertiesSet();

        return partitionHandler;
    }

    @Bean
    public MessagingTemplate messageTemplate() {
        MessagingTemplate messagingTemplate = new MessagingTemplate(outboundRequest);
        messagingTemplate.setReceiveTimeout(60000000l);
        return messagingTemplate;
    }

    @Bean
    @Qualifier("outboundRequest")
    public DirectChannel outboundRequests() {
        return new DirectChannel();
    }

    @StepScope
    @Bean
    @Qualifier("itemReader")
    public ItemReader<Asset> itemReader() throws Exception {
        log.info(".................. ItemReader");
        log.info("sleep .................. 9000");
        //Thread.sleep(9000);
        log.info("end sleep .................. 9000");
        return new CustomerItemReader("resource");
    }

    @Bean
    @Qualifier("itemWriter")
    public ItemWriter<Asset> itemWriter() {
        log.info(".................. ItemReader");
        return assets -> {
            if (assets != null && !assets.isEmpty()) {
                log.info("Write: {}", assets);
                // assetKVRepository.saveAll(assets);
            }
        };
    }

}
*/