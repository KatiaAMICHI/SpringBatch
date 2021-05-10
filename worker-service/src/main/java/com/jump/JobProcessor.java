package com.jump;

import com.jump.objects.jobObject.JobEvent;
import com.jump.objects.asset.Asset;
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

@EnableBinding(Processor.class)
@Slf4j
public class JobProcessor {

    @Autowired
    private Processor processor;

    @StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='processing'")
    public void listenInfos(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent in = parMsg.getPayload();
        final int parPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker PROCESSING] received message : " + in + ", from partition : " + parPartition);
    }

    @StreamListener(target = Processor.INPUT, condition = "headers['custom_info']=='start'")
    public void listenStart(final Message<JobEvent> parMsg) throws InterruptedException {
        final JobEvent in = parMsg.getPayload();
        final int locPartition = (int)parMsg.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID);
        log.info("[Worker START] received message : " + in + ", from partition : " + locPartition);

        // envoyer un message d'avancement
         Message<JobEvent> partitionKey = MessageBuilder.withPayload(in)
                                                        .setHeader("custom_info", "infos")
                                                        .setHeader("worker_partition", locPartition)
                                                        .build();
        processor.output().send(partitionKey);
        Thread.sleep(20000);
        log.info("[Worker] received message - end sleep 10 s");
        final Asset locResult = getResult(in.getPath());

        in.setStatus(BatchStatus.COMPLETED);
        in.setExitStatus("COMPLETED");

        partitionKey = MessageBuilder.withPayload(in)
                                     .setHeader("custom_info", "end")
                                     .build();
        log.info("[Worker START] sendding message to MASTER SERVER");
        processor.output().send(partitionKey);
    }

    private Asset getResult(final String parUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(parUrl, Asset.class);
    }

}