package com.jump.remotechunking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;

import java.util.List;


@Slf4j
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
//@Profile("worker")
public class WorkerChunkingJob {

    @Autowired
    private RemoteChunkingWorkerBuilder remoteChunkingWorkerBuilder;
    @Autowired
    private ConsumerFactory kafkaFactory;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private DirectChannel requests_chunking;
    @Autowired
    private QueueChannel replies_chunking;

    /**
     * ecouter les message du worker manager
     *
     * @return
     */
    @Bean
    public IntegrationFlow inboundFlowWorker() {
        final ContainerProperties containerProps = new ContainerProperties(ChunkingConfig.TOPIC_REQUESTS);
        containerProps.setGroupId(ChunkingConfig.GROUP_ID);

        final KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(kafkaFactory, containerProps);
        final KafkaMessageDrivenChannelAdapter kafkaMessageChannel = new KafkaMessageDrivenChannelAdapter(container);

        return IntegrationFlows
                //.from(Kafka.messageDrivenChannelAdapter(container))
                .from(kafkaMessageChannel)
                .channel(requests_chunking)
                .get();
    }

    /**
     * envoyer des message au worker slave (apres traitement des items (process / write)
     *
     * @return
     */
    @Bean
    public IntegrationFlow outboundFlowWorker() {
        final KafkaProducerMessageHandler kafkaMessageHandler = new KafkaProducerMessageHandler(kafkaTemplate);
        kafkaMessageHandler.setTopicExpression(new LiteralExpression(ChunkingConfig.TOPIC_REPLIES));
        kafkaMessageHandler.setSendSuccessChannel(replies_chunking);
        kafkaMessageHandler.setOutputChannel(replies_chunking);

        return IntegrationFlows
                .from(replies_chunking)
                //.handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic(TOPIC_REPLIES))
                .handle(kafkaMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow workerIntegrationFlow() {
        return this.remoteChunkingWorkerBuilder
                .inputChannel(requests_chunking)
                .outputChannel(replies_chunking)
                .itemProcessor(itemProcessor())
                .itemWriter(itemWriter())
                .build();
    }

    public ItemWriter<? super Object> itemWriter() {
        SynchronizedItemStreamWriter<Object> synchronizedItemStreamWriter = new SynchronizedItemStreamWriter();
        log.info(".................. itemWriter");
        final ItemStreamWriter<Object> writer = new Writer1();

        synchronizedItemStreamWriter.setDelegate(writer);
        return writer;
    }

    public ItemProcessor<Object, Object> itemProcessor() {
        log.info(".................. processor");
        return new ItemProcessor<Object, Object>() {
            @Override
            public Object process(Object parObj) throws Exception {
                //parObj.setLabel("newLabel");
                log.info("Pause for 6 seconds parObj :" + parObj);
                Thread.sleep(10000);
                return parObj;
            }
        };
    }

}
