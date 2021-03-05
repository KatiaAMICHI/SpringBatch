package com.jump.configuration;

import com.jump.domain.Asset;
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
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
    public Tasklet tasklet(@Value("#{stepExecutionContext['path']}") final String path) {
        return (contribution, chunkContext) -> {
            System.out.println("processing " + path);
            final String locParameter = (String) chunkContext.getStepContext().getJobParameters().get("value");
            Asset locResult = getResult(path + locParameter);
            log.info("[Worker] read, result : " + locResult);
            // Thread.sleep(9000);
            outboudChannel.send(new GenericMessage<>("send message from Worker, result : " + locResult));
            // Thread.sleep(9000);
            return RepeatStatus.FINISHED;
        };
    }

    public Asset getResult(final String parUrl) {
        // String serverString = "http://localhost:50101/asset/get?parLabel=asset_label";
        RestTemplate restTemplate = new RestTemplate();
        Asset result = restTemplate.getForObject(parUrl, Asset.class);

        return result;
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
