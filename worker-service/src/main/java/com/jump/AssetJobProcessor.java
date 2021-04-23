package com.jump;

import com.jump.objects.JobEvent;
import com.jump.objects.asset.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.client.RestTemplate;

@EnableBinding(Processor.class)
@Slf4j
public class AssetJobProcessor {

    @StreamListener(Processor.INPUT) @SendTo(Processor.OUTPUT)
    public JobEvent listen(@Payload final JobEvent in, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int parPartition) throws InterruptedException {
        log.info("[Worker] received message : " + in + ", from partition " + parPartition);
        Thread.sleep(20000);
        log.info("[Worker] received message - end sleep 10 s");
        final Asset locResult = getResult(in.getPath());

        log.info("[Worker] result : " + locResult);

        in.setStatus(BatchStatus.COMPLETED);
        in.setExitStatus("COMPLETED");

        return in;
    }

    public Asset getResult(final String parUrl) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(parUrl, Asset.class);
    }

}