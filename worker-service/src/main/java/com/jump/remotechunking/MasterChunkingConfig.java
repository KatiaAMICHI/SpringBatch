package com.jump.remotechunking;

import com.jump.configs.CustomChunkListener;
import com.jump.objects.jobObject.JobEvent;
import com.jump.remotepartition.CustomerItemReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
//@Profile("master")
public class MasterChunkingConfig {


    @Autowired
    private ConsumerFactory kafkaFactory;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
	private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private RemoteChunkingManagerStepBuilderFactory masterStepBuilderFactory;
    @Autowired
    private DirectChannel requests_chunking;
    @Autowired
    private QueueChannel replies_chunking;

    /**
     * envoyer des message au worker slave, pour executer le process/write items apres le read
     * @return
     */
    @Bean
    public IntegrationFlow outboundFlowMaster() {
        final KafkaProducerMessageHandler kafkaMessageHandler = new KafkaProducerMessageHandler(kafkaTemplate);
        kafkaMessageHandler.setTopicExpression(new LiteralExpression(ChunkingConfig.TOPIC_REQUESTS));

        return IntegrationFlows
                .from(requests_chunking)
                //.handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic(TOPIC_REQUESTS))
                .handle(kafkaMessageHandler)
                .get();
    }

    /**
     * ecouter le retour des messages du worker slave
     *
     * @return
     */
    @Bean
    public IntegrationFlow inboundFlowMaster() {
        final ContainerProperties containerProps = new ContainerProperties(ChunkingConfig.TOPIC_REPLIES);
        containerProps.setGroupId(ChunkingConfig.GROUP_ID);
        final KafkaMessageListenerContainer container = new KafkaMessageListenerContainer(kafkaFactory, containerProps);
        final KafkaMessageDrivenChannelAdapter kafkaMessageChannel = new KafkaMessageDrivenChannelAdapter(container);


        return IntegrationFlows
                //.from(Kafka.messageDrivenChannelAdapter(container))
                .from(kafkaMessageChannel)
                .channel(replies_chunking)
                .get();
}

	@Bean
	public Job remoteJob() {
		return jobBuilderFactory
                .get("remoteJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep_ch())
				.build();
	}

    @Bean
    public TaskletStep masterStep_ch() {
        final TaskExecutorRepeatTemplate repeatTemplate = new TaskExecutorRepeatTemplate();

        final SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        taskExecutor.setThreadPriority(10);
        return  masterStepBuilderFactory
                .get("masterStep_ch")
                .chunk(1)
                .outputChannel(requests_chunking) // requests sent to workers
                .inputChannel(replies_chunking)   // replies received from workers
                .reader(itemReader())
                .taskExecutor(taskExecutor)
                //.listener(new CustomChunkListener())
                .stepOperations(repeatTemplate)
                //.repository(jobRepository)
                .build();
    }


    /*@Bean
    public MultiResourceItemReader<String> multiResourceItemReader() {
        MultiResourceItemReader<String> resourceItemReader = new MultiResourceItemReader<String>();
        resourceItemReader.setResources(resources); // can passe multipl files
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }*/

    @Bean
    @StepScope
    public ItemReader<String> itemReader() {
        SynchronizedItemStreamReader<String> synchronizedItemStreamReader = new SynchronizedItemStreamReader();
        log.info("[MASTER Worker].................. ItemReader");
        final FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        reader.setResource(new ClassPathResource("data/fileTMP.txt"));
        reader.setLineMapper(new PassThroughLineMapper());
        reader.open(new ExecutionContext());
        synchronizedItemStreamReader.setDelegate(reader);


        return synchronizedItemStreamReader;
    }


}