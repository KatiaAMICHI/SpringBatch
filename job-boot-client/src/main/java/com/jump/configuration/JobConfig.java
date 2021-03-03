package com.jump.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.GenericMessage;

@Configuration
@IntegrationComponentScan
@Slf4j
public class JobConfig {

    @Autowired
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
    @Autowired
    @Qualifier("inbound")
    private DirectChannel inboundChannel;
    @Autowired
    @Qualifier("outbound")
    private QueueChannel outboudChannel;

    /*
     * Configure the worker step
     */
    @Bean
    public Step workerStep() {
        return this.workerStepBuilderFactory.get("workerStep")
                                            .inputChannel(inboundChannel)
                                            .outputChannel(outboudChannel)
                                            .tasklet(tasklet(null))
                                            .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{stepExecutionContext['partition']}") String partition) {
        return (contribution, chunkContext) -> {
            System.out.println("processing " + partition);
            log.info("[Worker]... processing " + partition);
            // Thread.sleep(9000);
            outboudChannel.send(new GenericMessage<>("send message from Worker : " + partition));
            // Thread.sleep(9000);
            return RepeatStatus.FINISHED;
        };
    }


    /*@StepScope
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
    @ServiceActivator(inputChannel = "requests", outputChannel = "replies")
    public ChunkProcessorChunkHandler<Integer> chunkProcessorChunkHandler() {
        // je peut réupérer un truc lomda pour itemProcessor & itemWritter
        ChunkProcessor<Integer> chunkProcessor = new SimpleChunkProcessor<>(itemProcessor(), itemWriter());
        ChunkProcessorChunkHandler<Integer> chunkProcessorChunkHandler
                = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(chunkProcessor);
        return chunkProcessorChunkHandler;
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
