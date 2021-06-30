package com.jump.customjobservice;

import com.jump.objects.asset.Asset;
import com.jump.objects.jobObject.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Arrays;

/**
 * permet d’ecouter / d’envoyer des messages du « server-queue »
 *      Processor.input : pour ecouter
 *      Processor.output : pour envoie
 */
@EnableBinding(Sink.class)
@Slf4j
public class JobProcessor {

    @Autowired
    private ConsumerFactory consumerFactory;

    //DistrubutedSet<Integer> _consumerLock;
    //Map<Integer, BatchContext> _executingBatch;

   //@Async
   //@Scheduled(initialDelay = 0, fixedDelay = 2000)
    public void processKafkaRecords() throws InterruptedException {
        Consumer<byte[], byte[]> consumer = consumerFactory.createConsumer("product", "consumer-product-2", null, null);
        //log.info("--- [processKafkaRecords] consumer : " + consumer);
        consumer.subscribe(Arrays.asList("workern"));
        ConsumerRecords<byte[], byte[]> poll = consumer.poll(Duration.ofMillis(1));
        log.info("--- [processKafkaRecords] poll : " + poll.iterator().hasNext());
        poll.forEach(record -> {
            log.info("record {}", record);
        });

    }

    @StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='processing' ")
    public void listenInfos(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent locPayload = parMsg.getPayload();
        final int locPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker PROCESSING] received message : " + locPayload + ", from partition : " + locPartition);
    }

    /*@EventListener
    public void onIdleEvent(ListenerContainerIdleEvent event) {
        log.info("[----- Worker EventListener] received message : " + event);
        System.out.println(" -----  EventListener : "  + event);
    }*/

    static private KafkaConsumer consumer;

    //@StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='start' && @customFilter.stratfun('star', headers['kafka_consumer'])")
    @StreamListener(target = Sink.INPUT, condition = "headers['custom_info']=='start'")
    public void listenStart(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent locPayload = parMsg.getPayload();
        final int locPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker START] received message from partition {}, {} : ", locPartition, locPayload);
        consumer = (KafkaConsumer) parMsg.getHeaders().get(KafkaHeaders.CONSUMER);

        if (false) {
            //((Acknowledgment)parMsg.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT)).nack(10000);
            consumer.commitSync();
            //consumer.unsubscribe();
            consumer.close(Duration.ofMillis(1000));
            //bindingsEndpoint.changeState("input", BindingsEndpoint.State.STOPPED);
            return;
        }

        log.info("[Worker START] received message from partition {}, {} : ", locPartition, locPayload);

        // envoyer un message d'avancement
         final Message<JobEvent> locMessageInfos = MessageBuilder.withPayload(locPayload)
                                                        .setHeader("custom_info", "infos")
                                                        .setHeader("worker_partition", locPartition)
                                                        .build();
        //locPartition.put(locPartition);
        //_executingBatch.add(locPayload.getJobId());
        //_sink.output().send(locMessageInfos);
        Thread.sleep(4000);

        log.info("[Worker] received message - end sleep 10 s");
        //final Asset locResult = getResult(locPayload.getPath());

        locPayload.setStatus(BatchStatus.COMPLETED);
        locPayload.setExitStatus("COMPLETED");

        final Message<JobEvent> locMessageEnd = MessageBuilder.withPayload(locPayload)
                                     .setHeader("custom_info", "end")
                                     .build();
        log.info("[Worker START END] sending message to MASTER SERVER");
        //_sink.output().send(locMessageEnd);
    }

    private Asset getResult(@NotNull final String parUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(parUrl, Asset.class);
    }



}