package com.jump;

import com.jump.objects.asset.Asset;
import com.jump.objects.jobObject.JobEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;

/**
 * permet d’ecouter / d’envoyer des messages du « server-queue »
 *      Processor.input : pour ecouter
 *      Processor.output : pour envoie
 */
@EnableBinding(Processor.class)
@Slf4j
public class JobProcessor {

    @Autowired
    private Processor processor;

    @StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='processing'")
    public void listenInfos(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent locPayload = parMsg.getPayload();
        final int locPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker PROCESSING] received message : " + locPayload + ", from partition : " + locPartition);
    }

    @StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='start'")
    public void listenStart(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent locPayload = parMsg.getPayload();
        final int locPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker START] received message from partition {}, {} : ", locPartition, locPayload);

        // envoyer un message d'avancement
         final Message<JobEvent> locMessageInfos = MessageBuilder.withPayload(locPayload)
                                                        .setHeader("custom_info", "infos")
                                                        .setHeader("worker_partition", locPartition)
                                                        .build();
        processor.output().send(locMessageInfos);
        Thread.sleep(20000);
        log.info("[Worker] received message - end sleep 10 s");
        final Asset locResult = getResult(locPayload.getPath());

        locPayload.setStatus(BatchStatus.COMPLETED);
        locPayload.setExitStatus("COMPLETED");

        final Message<JobEvent> locMessageEnd = MessageBuilder.withPayload(locPayload)
                                     .setHeader("custom_info", "end")
                                     .build();
        log.info("[Worker START] sending message to MASTER SERVER");
        processor.output().send(locMessageEnd);
    }

    private Asset getResult(@NotNull final String parUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(parUrl, Asset.class);
    }

}